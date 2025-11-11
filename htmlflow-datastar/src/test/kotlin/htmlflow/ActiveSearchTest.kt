package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class ActiveSearchTest {
    @Test
    fun `Active Search of the Datastar Frontend Reactivity`() {
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
                    input{
                        attrType(EnumTypeInputType.TEXT)
                        attrPlaceholder("Search...")
                        dataBind("search", null)
                        dataOn("input__debounce.200ms", "@get('/examples/active_search/search')")
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
    <input type="text" placeholder="Search..." data-bind-search="" data-on-input__debounce.200ms="@get('/examples/active_search/search')">
</body>
</html>
    """
}