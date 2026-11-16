@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.CustomEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class SortableTest {
    @Test
    fun `Sortable of the Datastar Frontend Reactivity`() {
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
                                val orderInfo = dataSignal("order-info", "Initial order")
                                dataText { +orderInfo }
                            }
                            div {
                                attrId("sortContainer")
                                val reorderedEvent = CustomEvent("reordered")
                                dataOn(reorderedEvent) {
                                    +$$"$orderInfo = event.detail.orderInfo"
                                }
                                for (i in 1 until 6) {
                                    button { text("Item $i") }
                                }
                            }
                            script {
                                attrType(EnumTypeScriptType.MODULE)
                                raw(
                                    $$"""
                            import Sortable from 'https://cdn.jsdelivr.net/npm/sortablejs/+esm'
                            new Sortable(sortContainer, {
                                animation: 150,
                                ghostClass: 'opacity-25',
                                onEnd: (evt) => {
                                    sortContainer.dispatchEvent(
                                        new CustomEvent('reordered', {detail: {
                                            orderInfo: `Moved from position ${evt.oldIndex + 1} to ${evt.newIndex + 1}`
                                        }})
                                    )
                                }
                            })
                                    """.trimIndent(),
                                )
                            }
                        }
                    }
                }
            }

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div data-signals="{'order-info': 'Initial order'}" data-text="$order-info"> 
    </div>
    <div id="sortContainer" data-on:reordered="$orderInfo = event.detail.orderInfo">
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
                        orderInfo: `Moved from position ${evt.oldIndex + 1} to ${evt.newIndex + 1}`
                    }})
                )
            }
        })
    </script>
</body>
</html>
    """
}
