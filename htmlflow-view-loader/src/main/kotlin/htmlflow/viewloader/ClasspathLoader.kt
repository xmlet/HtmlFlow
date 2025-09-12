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
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Bernardo Pereira
 */
@Suppress("UNCHECKED_CAST")
class ClasspathLoader<T : Any>(private val type: Class<T> = Any::class.java as Class<T>) {

    private val classpathScanner = ClasspathScanner()
    private val classInspector = ClassInspector()
    private val viewResolver = ViewResolver<T>()

    private val cachedViewRegistries = ConcurrentHashMap<String, Map<Class<*>, ViewInfo>>()
    private val hotViewRegistries = ConcurrentHashMap<String, Map<Class<*>, ViewInfo>>()

    /**
     * Scans the classpath for views of type [HtmlView] compatible with model type [T].
     */
    fun scanForViews(
        basePackage: String = "",
        hotReload: Boolean = false,
        factory: HtmlFlow.ViewFactory? = null
    ): Map<Class<*>, ViewInfo> {
        val cacheKey = "$basePackage:$hotReload:${factory?.hashCode() ?: 0}"
        val targetRegistry = if (hotReload) hotViewRegistries else cachedViewRegistries

        return targetRegistry.computeIfAbsent(cacheKey) { _ ->
            performScan(basePackage, hotReload, factory)
        }
    }

    /**
     * Creates a renderer function that takes a model of type [T] and returns the rendered HTML string.
     *
     * @param basePackage The base package to scan for views. Defaults to the root package.
     * @param hotReload If true, views will be re-scanned on each render call
     * @param factory An optional [HtmlFlow.ViewFactory] to be passed to views.
     *
     * @return A function that takes a model of type [T] and returns the rendered HTML string.
     */
    fun createRenderer(
        basePackage: String = "",
        hotReload: Boolean = false,
        factory: HtmlFlow.ViewFactory? = null
    ): (T) -> String {
        val viewRegistry = scanForViews(basePackage, hotReload, factory)
        return { model ->
            val viewInfo = viewResolver.findCompatibleView(model, viewRegistry)
            renderViewWithModel(viewInfo.view, model, viewInfo.location)
        }
    }

    private fun performScan(
        basePackage: String,
        hotReload: Boolean,
        factory: HtmlFlow.ViewFactory?
    ): Map<Class<*>, ViewInfo> {
        val viewRegistry = mutableMapOf<Class<*>, ViewInfo>()

        classpathScanner.scanPackage(basePackage) { className ->
            loadAndScanClass(className, viewRegistry, hotReload, factory)
        }

        return viewRegistry.toMap()
    }

    private fun loadAndScanClass(
        className: String,
        viewRegistry: MutableMap<Class<*>, ViewInfo>,
        hotReload: Boolean,
        factory: HtmlFlow.ViewFactory?
    ) {
        try {
            val clazz = Class.forName(className)
            val views = classInspector.inspectClass(clazz, type, hotReload, factory)

            views.forEach { viewInfo ->
                checkForDuplicateViewRegistration(viewRegistry, viewInfo.modelType, viewInfo.location)
                viewRegistry[viewInfo.modelType] = viewInfo
            }
        } catch (_: ClassNotFoundException) {
            // no-op
        } catch (_: NoClassDefFoundError) {
            // no-op
        } catch (e: Exception) {
            throw e
        }
    }

    private fun checkForDuplicateViewRegistration(
        viewRegistry: Map<Class<*>, ViewInfo>,
        modelType: Class<*>,
        newLocation: String
    ) {
        val existing = viewRegistry[modelType]
        if (existing == null) return
        throw IllegalStateException(
            "Multiple views found for model type '${modelType.simpleName}'. " +
                    "Existing: ${existing.location}, New: $newLocation"
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun renderViewWithModel(view: Any, model: T, location: String): String {
        return when (view) {
            is HtmlView<*> -> {
                (view as HtmlView<T>).render(model)
            }
            else -> throw IllegalArgumentException(
                "Unsupported view type: ${view::class.simpleName} at $location"
            )
        }
    }
}
