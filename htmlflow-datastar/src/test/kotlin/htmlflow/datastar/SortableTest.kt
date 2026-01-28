package htmlflow.datastar

import htmlflow.html
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class SortableTest {
    @Test
    fun `Sortable of the Datastar Frontend Reactivity`() {
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
                        val orderInfo = dataSignal("order-info", "Initial order")
                        dataText("$orderInfo")
                    }
                    div {
                        attrId("sortContainer")
                        dataOn("reordered", "${'$'}orderInfo = event.detail.orderInfo")
                        button { +"Item 1" }
                        button { +"Item 2" }
                        button { +"Item 3" }
                        button { +"Item 4" }
                        button { +"Item 5" }
                    }
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        +
                            """
                            import Sortable from 'https://cdn.jsdelivr.net/npm/sortablejs/+esm'
                            new Sortable(sortContainer, {
                                animation: 150,
                                ghostClass: 'opacity-25',
                                onEnd: (evt) => {
                                    sortContainer.dispatchEvent(
                                        new CustomEvent('reordered', {detail: {
                                            orderInfo: `Moved from position ${'$'}{evt.oldIndex + 1} to ${'$'}{evt.newIndex + 1}`
                                        }})
                                    )
                                }
                            })
                            """.trimIndent()
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
    <div data-signals-order-info="'Initial order'" data-text="${'$'}order-info"> 
    </div>
    <div id="sortContainer" data-on-reordered="${'$'}orderInfo = event.detail.orderInfo">
        <button>
            Item 1
        </button>
        <button>
            Item 2
        </button>
        <button>
            Item 3
        </button>
        <button>
            Item 4
        </button>
        <button>
            Item 5
        </button>
    </div>
    <script type="module">
        import Sortable from 'https://cdn.jsdelivr.net/npm/sortablejs/+esm'
        new Sortable(sortContainer, {
            animation: 150,
            ghostClass: 'opacity-25',
            onEnd: (evt) => {
                sortContainer.dispatchEvent(
                    new CustomEvent('reordered', {detail: {
                        orderInfo: `Moved from position ${'$'}{evt.oldIndex + 1} to ${'$'}{evt.newIndex + 1}`
                    }})
                )
            }
        })
    </script>
</body>
</html>
    """
}
