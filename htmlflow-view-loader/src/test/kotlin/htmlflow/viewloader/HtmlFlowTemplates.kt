package htmlflow.viewloader

import htmlflow.HtmlFlow

typealias TemplateRenderer = (ViewModel) -> String

inline fun <reified T : ViewModel> htmlflow.HtmlView<T>.renderer(): TemplateRenderer {
    return { viewModel: ViewModel ->
        try {
            @Suppress("UNCHECKED_CAST") this.render(viewModel as T)
        } catch (_: ClassCastException) {
            throw ViewNotFound(viewModel)
        }
    }
}

interface Templates {

    /**
     * Loads and caches templates from the compiled classpath
     *
     * @param baseClasspathPackage the root package to load from (defaults to root)
     */
    fun CachingClasspath(baseClasspathPackage: String = ""): TemplateRenderer

    /**
     * Load and caches templates from a file path
     *
     * @param baseTemplateDir the root path to load templates from
     */
    fun Caching(baseTemplateDir: String = "./"): TemplateRenderer

    /**
     * Hot-reloads (no-caching) templates from a file path
     *
     * @param baseTemplateDir the root path to load templates from
     */
    fun HotReload(baseTemplateDir: String = "./"): TemplateRenderer
}

class HtmlFlowTemplates: Templates {

    private val loader = ClasspathLoader(ViewModel::class.java)

    private val factory = HtmlFlow.ViewFactory
        .builder()
        .threadSafe(true)
        .preEncoding(true)

    override fun CachingClasspath(baseClasspathPackage: String): TemplateRenderer {
        return loader.createRenderer(baseClasspathPackage, hotReload = false, factory = factory.build())
    }

    override fun Caching(baseTemplateDir: String): TemplateRenderer {
        val basePackage = convertPathToPackage(baseTemplateDir)
        return loader.createRenderer(basePackage, hotReload = false, factory = factory.build())
    }

    override fun HotReload(baseTemplateDir: String): TemplateRenderer {
        val basePackage = convertPathToPackage(baseTemplateDir)
        return loader.createRenderer(basePackage, hotReload = true, factory = factory.preEncoding(false).build())
    }

    private fun convertPathToPackage(path: String): String {
        return path
            .replace(Regex("^(src[/\\\\](main|test)[/\\\\]kotlin([/\\\\])?)"), "")
            .replace(Regex("[/\\\\]"), ".")
            .trim('.')
    }
}