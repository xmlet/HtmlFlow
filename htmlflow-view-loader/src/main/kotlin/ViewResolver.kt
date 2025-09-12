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

import java.util.concurrent.ConcurrentHashMap

/**
 * @author Bernardo Pereira
 */
class ViewResolver<T> {
    private val resolutionCache = ConcurrentHashMap<Class<*>, ViewInfo>()

    fun findCompatibleView(
        model: T,
        viewRegistry: Map<Class<*>, ViewInfo>,
    ): ViewInfo {
        val modelClass = model!!::class.java

        viewRegistry[modelClass]?.let { return it }

        resolutionCache[modelClass]?.let { return it }

        val resolved = resolveViewThroughHierarchy(modelClass, viewRegistry)

        resolved?.let { resolutionCache[modelClass] = it }

        return resolved ?: throw ViewNotFound(model)
    }

    private fun resolveViewThroughHierarchy(
        modelClass: Class<*>,
        viewRegistry: Map<Class<*>, ViewInfo>,
    ): ViewInfo? {
        findViewInInheritanceChain(modelClass, viewRegistry)?.let { return it }

        findViewInInterfaces(modelClass, viewRegistry)?.let { return it }

        return null
    }

    private fun findViewInInheritanceChain(
        modelClass: Class<*>,
        viewRegistry: Map<Class<*>, ViewInfo>,
    ): ViewInfo? {
        var currentClass: Class<*>? = modelClass.superclass
        while (currentClass != null && currentClass != Any::class.java) {
            viewRegistry[currentClass]?.let { return it }
            currentClass = currentClass.superclass
        }
        return null
    }

    private fun findViewInInterfaces(
        modelClass: Class<*>,
        viewRegistry: Map<Class<*>, ViewInfo>,
    ): ViewInfo? {
        for (interfaceClass in modelClass.interfaces) {
            viewRegistry[interfaceClass]?.let { return it }
        }
        return null
    }
}
