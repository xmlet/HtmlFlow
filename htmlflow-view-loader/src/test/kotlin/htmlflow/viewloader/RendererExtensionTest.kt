package htmlflow.viewloader

import htmlflow.HtmlFlow
import htmlflow.dyn
import htmlflow.html
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h1
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.li
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.title
import org.xmlet.htmlapifaster.ul

class RendererExtensionTest {
    @Nested
    inner class HtmlViewExtensionTests {
        @Test
        fun `should create renderer from HtmlView`() {
            val view: htmlflow.HtmlView<SimpleTestViewModel> =
                HtmlFlow.view {
                    it.html {
                        body {
                            div {
                                attrClass("test-view")
                                h1 { text("Test View") }
                                dyn { model: SimpleTestViewModel ->
                                    p { text("Content: ${model.content}") }
                                }
                            }
                        }
                    }
                }

            val renderer = view.renderer()
            assertNotNull(renderer)

            val model = SimpleTestViewModel("Hello World")
            val result = renderer(model)

            assertTrue(result.contains("<html>"))
            assertTrue(result.contains("Test View"))
            assertTrue(result.contains("Content: Hello World"))
            assertTrue(result.contains("</html>"))
        }

        @Test
        fun `should handle complex HTML structure in renderer`() {
            val view: htmlflow.HtmlView<ComplexTestViewModel> =
                HtmlFlow.view {
                    it.html {
                        head { title { text("Complex Test") } }
                        body {
                            div {
                                attrClass("container")
                                dyn { model: ComplexTestViewModel ->
                                    h1 { text(model.title) }
                                    if (model.isActive) {
                                        div {
                                            attrClass("status active")
                                            text("Status: Active")
                                        }
                                    } else {
                                        div {
                                            attrClass("status inactive")
                                            text("Status: Inactive")
                                        }
                                    }
                                    ul {
                                        attrClass("items")
                                        model.items.forEach { item ->
                                            li {
                                                attrClass("item")
                                                text(item)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            val renderer = view.renderer()
            val model =
                ComplexTestViewModel(
                    title = "My Complex View",
                    items = listOf("First Item", "Second Item", "Third Item"),
                    isActive = true,
                )

            val result = renderer(model)

            assertTrue(result.contains("Complex Test"))
            assertTrue(result.contains("My Complex View"))
            assertTrue(result.contains("Status: Active"))
            assertTrue(result.contains("First Item"))
            assertTrue(result.contains("Second Item"))
            assertTrue(result.contains("Third Item"))
            assertTrue(result.contains("class=\"container\""))
            assertTrue(result.contains("class=\"items\""))
        }

        @Test
        fun `should handle inactive status in complex view`() {
            val view: htmlflow.HtmlView<ComplexTestViewModel> =
                HtmlFlow.view {
                    it.html {
                        body {
                            dyn { model: ComplexTestViewModel ->
                                if (model.isActive) {
                                    div { text("Active Content") }
                                } else {
                                    div { text("Inactive Content") }
                                }
                            }
                        }
                    }
                }

            val renderer = view.renderer()
            val model = ComplexTestViewModel("Title", emptyList(), false)
            val result = renderer(model)

            assertTrue(result.contains("Inactive Content"))
            assertFalse(result.contains("Active Content"))
        }

        @Test
        fun `should throw exception for type mismatch in HtmlView renderer`() {
            val view: htmlflow.HtmlView<SimpleTestViewModel> = HtmlFlow.view { it.html { body {} } }

            val renderer = view.renderer()
            val wrongModel = ComplexTestViewModel("title", emptyList(), false)

            val exception = assertThrows<ViewNotFound> { renderer(wrongModel) }

            assertTrue(exception.message!!.contains("No view found for model type"))
        }

        @Test
        fun `should handle empty content gracefully`() {
            val view: htmlflow.HtmlView<SimpleTestViewModel> =
                HtmlFlow.view {
                    it.html {
                        body {
                            dyn { model: SimpleTestViewModel ->
                                if (model.content.isNotEmpty()) {
                                    p { text(model.content) }
                                }
                            }
                        }
                    }
                }

            val renderer = view.renderer()
            val model = SimpleTestViewModel("")
            val result = renderer(model)

            assertTrue(result.contains("<html>"))
            assertTrue(result.contains("<body>"))
            assertTrue(result.contains("</body>"))
            assertTrue(result.contains("</html>"))
            assertFalse(result.contains("<p>"))
        }
    }
}
