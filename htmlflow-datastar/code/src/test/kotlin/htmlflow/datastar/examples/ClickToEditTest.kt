@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class ClickToEditTest {
    @Test
    fun `Click To Edit of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        StringBuilder()
            .apply {
                doc {
                    html {
                        head {
                            script {
                                attrType(EnumTypeScriptType.MODULE)
                                attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                            }
                        }
                        body {
                            div {
                                attrId("demo")
                                p { text("First Name: John") }
                                p { text("Last Name: Doe") }
                                p { text("Email: joe@blow.com") }
                                div {
                                    button {
                                        attrClass("info")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                        dataOn(Click) {
                                            get(::edit)
                                        }
                                        text("Edit")
                                    }
                                    button {
                                        attrClass("warning")
                                        val fetching = dataIndicator("_fetching")
                                        dataAttr("disabled") { +fetching }
                                        dataOn(Click) {
                                            patch(::reset)
                                        }
                                        text("Reset")
                                    }
                                }
                            }
                        }
                    }
                }
            }

    @Path("/examples/click_to_edit/edit")
    private fun edit() {}

    @Path("/examples/click_to_edit/reset")
    private fun reset() {}

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo">
        <p>
            First Name: John
        </p>
        <p>
            Last Name: Doe
        </p>
        <p>
            Email: joe@blow.com
        </p>
        <div>
            <button class="info" data-indicator="_fetching" data-attr:disabled="$_fetching" data-on:click="@get('/examples/click_to_edit/edit')">
                Edit
            </button>
            <button class="warning" data-indicator="_fetching" data-attr:disabled="$_fetching" data-on:click="@patch('/examples/click_to_edit/reset')">
                Reset
            </button>
        </div>
    </div>
</body>
</html>
    """
}
