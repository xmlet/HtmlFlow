package htmlflow.viewloader

import ClasspathLoader
import ViewNotFound
import htmlflow.HtmlFlow
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class ClasspathScannerTest {

    @Test
    fun `test renderer with view factory`() {
        val loader = ClasspathLoader<Any>()
        val factory = HtmlFlow.ViewFactory.builder().threadSafe(false).preEncoding(false).build()
        val renderer = loader.createRenderer(basePackage = "htmlflow.viewloader.views.builder", factory = factory)
        val model = BuilderTestViewModel("Test Content")
        val output = renderer(model)
        assert(output.contains("Test Content"))
    }

    @Test
    fun `test renderer without view factory`() {
        val loader = ClasspathLoader<Any>()
        val renderer = loader.createRenderer(basePackage = "htmlflow.viewloader.views.builder")
        val model = BuilderTestViewModel("Test Content")
        assertThrows<ViewNotFound> { renderer(model) }
    }
}