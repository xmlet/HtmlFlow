package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import kotlin.test.assertEquals

class TemplCounterTest {
    @Test
    fun `Templ counter of the Datastar Frontend Reactivity`() {
        val out = StringBuilder()
        demoDastarRx.setOut(out).write()
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<Unit> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        dataInit("@get('/examples/templ_counter/updates')")
                        comment("Global Counter")
                        button {
                            attrId("global")
                            attrClass("info")
                            dataOn("click", "@patch('/examples/templ_counter/global')")
                            +"Global Clicks: 0"
                        }
                        comment("User Counter")
                        button {
                            attrId("user")
                            attrClass("success")
                            dataOn("click", "@patch('/examples/templ_counter/user')")
                            +"User Clicks: 0"
                        }
                    }
                }
            }
        }

    private val expectedDatastarRx = """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div data-init="@get('/examples/templ_counter/updates')">
                    <!-- Global Counter -->
                    <button id="global" class="info" data-on-click="@patch('/examples/templ_counter/global')">
                        Global Clicks: 0
                    </button>
                    <!-- User Counter -->
                    <button id="user" class="success" data-on-click="@patch('/examples/templ_counter/user')">
                        User Clicks: 0
                    </button>
                </div>
            </body>
        </html>
        """
}
