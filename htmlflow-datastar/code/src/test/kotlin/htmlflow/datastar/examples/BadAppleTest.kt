@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataInit
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import kotlin.test.Test
import kotlin.test.assertEquals

class BadAppleTest {
    @Test
    fun `BadApple of the Datastar Frontend Reactivity`() {
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
                                val percentage = dataSignal("percentage", 0)
                                val contents = dataSignal("contents", "bad apple frames go here")
                                label {
                                    dataInit { get(::badAppleUpdate) }
                                    span {
                                        dataText { +$$"`Percentage: ${$$percentage.toFixed(2)}%`" }
                                    }
                                    input {
                                        attrType(EnumTypeInputType.RANGE)
                                        attrMin("0")
                                        attrMax("100")
                                        attrStep("0.01")
                                        attrDisabled(true)
                                        attrStyle("cursor: default")
                                        dataAttr("value") { +percentage }
                                    }
                                }
                                pre {
                                    attrStyle("line-height: 100%")
                                    dataText { +contents }
                                }
                            }
                        }
                    }
                }
            }

    @Path("/examples/bad_apple/updates")
    private fun badAppleUpdate() {}

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div data-signals="{percentage: 0}" data-signals="{contents: 'bad apple frames go here'}"> 
       <label data-init="@get('/examples/bad_apple/updates')">
            <span data-text="`Percentage: ${$percentage.toFixed(2)}%`">
            </span>
            <input type="range" min="0" max="100" step="0.01" disabled="disabled" style="cursor: default" data-attr:value="$percentage">
       </label>
       <pre style="line-height: 100%" data-text="$contents">
       </pre>
    </div>
</body>
</html>
    """
}
