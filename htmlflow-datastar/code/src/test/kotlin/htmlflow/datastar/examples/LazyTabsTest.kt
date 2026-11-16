@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class LazyTabsTest {
    @Test
    fun `Lazy Tabs of the Datastar Frontend Reactivity`() {
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
                                div {
                                    addAttr("role", "tablist")
                                    buildButtons()
                                }
                                div {
                                    addAttr("role", "tabpanel")
                                    p { text("Lorem ipsum dolor sit amet...") }
                                    p { text("Consectetur adipiscing elit...") }
                                }
                            }
                        }
                    }
                }
            }

    fun Div<*>.buildButtons() {
        (0..2).forEach { i ->
            button {
                addAttr("role", "tab")
                addAttr("aria-selected", if (i == 0) "true" else "false")
                dataOn(Click) {
                    get("/examples/lazy_tabs/$i")
                }
                text("Tab $i")
            }
        }
    }

    @Path("/examples/lazy_tabs/0")
    private fun lazyTabs0() {}

    @Path("/examples/lazy_tabs/1")
    private fun lazyTabs1() {}

    @Path("/examples/lazy_tabs/2")
    private fun lazyTabs2() {}

    private val expectedDatastarRx = """
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo"> 
        <div role="tablist">
            <button role="tab" aria-selected="true" data-on:click="@get('/examples/lazy_tabs/0')">
                Tab 0
            </button>
            <button role="tab" aria-selected="false" data-on:click="@get('/examples/lazy_tabs/1')">
                Tab 1
            </button>
            <button role="tab" aria-selected="false" data-on:click="@get('/examples/lazy_tabs/2')">
                Tab 2
            </button>
        </div>
        <div role="tabpanel">
            <p>
                Lorem ipsum dolor sit amet...
            </p>
            <p>
                Consectetur adipiscing elit...
            </p>
        </div>
    </div>
</body>
</html>
    """
}
