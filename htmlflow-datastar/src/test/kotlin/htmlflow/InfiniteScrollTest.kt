package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import kotlin.test.assertEquals

class InfiniteScrollTest {
    @Test
    fun `Infinite Scroll of the Datastar Frontend Reactivity`() {
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
                        attrType(org.xmlet.htmlapifaster.EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        dataOnIntersect("@get('/examples/infinite_scroll/more')")
                        +"Loading..."
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
               <div data-on-intersect="@get('/examples/infinite_scroll/more')">
                    Loading...
               </div>
            </body>
        </html>        
        """
}
