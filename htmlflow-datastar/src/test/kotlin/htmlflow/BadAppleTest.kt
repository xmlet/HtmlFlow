package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class BadAppleTest {
    @Test
    fun `BadApple of the Datastar Frontend Reactivity`() {
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
                        val percentage = dataSignal("_percentage", 0)
                        val contents = dataSignal("_contents", "bad apple frames go here")
                        label {
                            dataInit("@get('/examples/bad_apple/updates')")
                            span { dataText("Percentage: ${'$'}{percentage.toFixed(2)}%") } //Needs to be checked
                            input {
                                attrType(EnumTypeInputType.RANGE)
                                attrMin("0")
                                attrMax("100")
                                attrStep("0.01")
                                attrDisabled(true)
                                attrStyle("cursor: default")
                                dataAttr("value", percentage)
                            }
                        }
                        pre {
                            attrStyle("line-height: 100%")
                            dataText("$contents")
                        }
                    }
                }
            }
        }

    private val expectedDatastarRx ="""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
        </script>
    </head>
<body>
    <div data-signals-_percentage="0" data-signals-_contents="'bad apple frames go here'"> 
       <label data-init="@get('/examples/bad_apple/updates')">
            <span data-text="Percentage: ${'$'}{percentage.toFixed(2)}%">
            </span>
            <input type="range" min="0" max="100" step="0.01" disabled="true" style="cursor: default" data-attr-value="${'$'}_percentage">
       </label>
       <pre style="line-height: 100%" data-text="${'$'}_contents">
       </pre>
    </div>
</body>
</html>
    """
}