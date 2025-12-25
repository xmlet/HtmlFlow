package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class ClickToLoadTest {
    @Test
    fun `Click To Load of the Datastar Frontend Reactivity`() {
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
                    button{
                        attrClass("info wide")
                        val fetching = dataIndicator("fetching")
                        dataAttr("aria-disabled", "`${'$'}{$fetching}`")
                        dataOn("click", "!$fetching && @get('/examples/click_to_load/more')")
                        +"Load More"
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
    <button class="info wide" data-indicator-fetching="" data-attr-aria-disabled="`${'$'}{${'$'}fetching}`" data-on-click="!${'$'}fetching && @get('/examples/click_to_load/more')">
        Load More
    </button>
</body>
</html>
    """
}