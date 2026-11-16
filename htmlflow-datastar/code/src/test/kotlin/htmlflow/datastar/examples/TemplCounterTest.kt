package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplCounterTest {
    @Test
    fun `Templ counter of the Datastar Frontend Reactivity`() {
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
                                dataInit { get(::getCounterUpdates) }
                                comment("Global Counter")
                                button {
                                    attrId("global")
                                    attrClass("info")
                                    dataOn(Click) {
                                        patch(::globalCounter)
                                    }
                                    text("Global Clicks: 0")
                                }
                                comment("User Counter")
                                button {
                                    attrId("user")
                                    attrClass("success")
                                    dataOn(Click) {
                                        patch(::userCounter)
                                    }
                                    text("User Clicks: 0")
                                }
                            }
                        }
                    }
                }
            }

    @Path("/examples/templ_counter/updates")
    private fun getCounterUpdates() {}

    @Path("/examples/templ_counter/global")
    private fun globalCounter() {}

    @Path("/examples/templ_counter/user")
    private fun userCounter() {}

    private val expectedDatastarRx = """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div data-init="@get('/examples/templ_counter/updates')">
                    <!-- Global Counter -->
                    <button id="global" class="info" data-on:click="@patch('/examples/templ_counter/global')">
                        Global Clicks: 0
                    </button>
                    <!-- User Counter -->
                    <button id="user" class="success" data-on:click="@patch('/examples/templ_counter/user')">
                        User Clicks: 0
                    </button>
                </div>
            </body>
        </html>
        """
}
