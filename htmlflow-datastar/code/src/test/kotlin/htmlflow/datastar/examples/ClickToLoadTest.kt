@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class ClickToLoadTest {
    @Test
    fun `Click To Load of the Datastar Frontend Reactivity`() {
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
                            button {
                                attrClass("info wide")
                                val fetching = dataIndicator("_fetching")
                                dataAttr("disabled") { +fetching }
                                dataOn(Click) {
                                    !fetching and get(::loadMore)
                                }
                                dataText { +"$fetching ? 'Loading...' : 'Load More'" }
                                text("Load More")
                            }
                        }
                    }
                }
            }

    @Path("/examples/click_to_load/more")
    private fun loadMore() {}

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <button class="info wide" data-indicator="_fetching" data-attr:disabled="$_fetching" data-on:click="!$_fetching && @get('/examples/click_to_load/more')" data-text="$_fetching ? 'Loading...' : 'Load More'">
        Load More
    </button>
</body>
</html>
    """
}
