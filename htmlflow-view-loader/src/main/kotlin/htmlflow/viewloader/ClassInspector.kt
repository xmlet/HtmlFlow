/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package htmlflow.viewloader

import htmlflow.HtmlFlow
import htmlflow.HtmlView
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author Bernardo Pereira
 */
class ClassInspector {

    fun <T> inspectClass(
        clazz: Class<*>,
        targetType: Class<T>,
        hot: Boolean,
        factory: HtmlFlow.ViewFactory? = null
    ): List<ViewInfo> {
        if (shouldSkipClass(clazz)) return emptyList()

        val views = mutableListOf<ViewInfo>()

        try {
            val objectInstance = createInstanceForScanning(clazz, factory)
            if (objectInstance == null && clazz.constructors.isNotEmpty()) {
                return emptyList()
            }
            views.addAll(scanMethodsForViews(clazz, objectInstance, targetType, hot))
        } catch (e: Exception) {
            throw e
        }

        return views
    }

    private fun createInstanceForScanning(
        clazz: Class<*>,
        factory: HtmlFlow.ViewFactory? = null
    ): Any? {
        if (clazz.name.endsWith("Kt")) {
            return null
        }

        return tryObjectInstance(clazz)
            ?: tryDefaultConstructor(clazz)
            ?: tryViewFactoryConstructor(clazz, factory)
            ?: tryCompanionObject(clazz)
            ?: trySingletonInstance(clazz)
    }

    private fun tryObjectInstance(clazz: Class<*>): Any? {
        return try {
            clazz.getDeclaredField("INSTANCE")[null]
        } catch (_: Exception) {
            null
        }
    }

    private fun tryCompanionObject(clazz: Class<*>): Any? {
        return try {
            val companionClass = clazz.declaredClasses.find { it.simpleName == "Companion" }
            companionClass?.getDeclaredField("INSTANCE")?.get(null)
        } catch (_: Exception) {
            null
        }
    }

    private fun trySingletonInstance(clazz: Class<*>): Any? {
        return try {
            clazz.getDeclaredMethod("getInstance").invoke(null)
        } catch (_: Exception) {
            null
        }
    }

    private fun tryDefaultConstructor(clazz: Class<*>): Any? {
        return try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (_: Exception) {
            null
        }
    }

    private fun tryViewFactoryConstructor(clazz: Class<*>, factory: HtmlFlow.ViewFactory?): Any? {
        if (factory == null) return null

        return try {
            val constructor = clazz.getDeclaredConstructor(HtmlFlow.ViewFactory::class.java)
            constructor.isAccessible = true
            constructor.newInstance(factory)
        } catch (_: NoSuchMethodException) {
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun <T> scanMethodsForViews(
        clazz: Class<*>,
        objectInstance: Any?,
        targetType: Class<T>,
        hot: Boolean
    ): List<ViewInfo> {
        return clazz.declaredMethods
            .filter { isRelevantViewMethod(it) }
            .mapNotNull { processViewMethod(it, objectInstance, targetType, hot) }
    }

    private fun <T> processViewMethod(
        method: Method,
        objectInstance: Any?,
        targetType: Class<T>,
        hot: Boolean
    ): ViewInfo? {
        method.isAccessible = true
        val originalView = method.invoke(objectInstance) ?: return null

        val location = "${method.declaringClass.name}.${method.name}()"
        val viewToRegister = (originalView as? HtmlView<*>)?.setPreEncoding(!hot) ?: return null

        return createViewInfo(viewToRegister, method.genericReturnType, targetType, location)
    }

    private fun <T> createViewInfo(
        view: Any,
        genericType: Type,
        targetType: Class<T>,
        location: String
    ): ViewInfo? {
        val modelType = extractModelType(genericType) ?: return null

        if (!targetType.isAssignableFrom(modelType) && !modelType.isInterface) {
            return null
        }

        return ViewInfo(view, location, modelType)
    }

    private fun extractModelType(genericType: Type): Class<*>? {
        return when (genericType) {
            is ParameterizedType -> extractFromParameterizedType(genericType)
            is Class<*> -> extractFromClassType(genericType)
            else -> null
        }
    }

    private fun extractFromParameterizedType(parameterizedType: ParameterizedType): Class<*>? {
        val typeArguments = parameterizedType.actualTypeArguments
        return if (typeArguments.isNotEmpty()) {
            typeArguments[0] as? Class<*>
        } else {
            null
        }
    }

    private fun extractFromClassType(classType: Class<*>): Class<*>? {
        val superType = classType.genericSuperclass
        return if (superType is ParameterizedType) {
            extractModelType(superType)
        } else {
            null
        }
    }

    private fun shouldSkipClass(clazz: Class<*>): Boolean {
        return clazz.isInterface || clazz.isAnnotation || clazz.isEnum
    }

    private fun isRelevantViewMethod(method: Method): Boolean {
        return !method.isSynthetic &&
                (method.parameterCount == 0 ||
                        (method.parameterCount == 1 &&
                                method.parameterTypes[0] == HtmlFlow.ViewFactory::class.java)) &&
                isHtmlViewMethod(method)
    }

    private fun isHtmlViewMethod(method: Method): Boolean =
        HtmlView::class.java.isAssignableFrom(method.returnType)
}
