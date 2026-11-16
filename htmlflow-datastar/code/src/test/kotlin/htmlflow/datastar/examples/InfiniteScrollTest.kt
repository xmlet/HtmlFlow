package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataOnIntersect
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import kotlin.test.Test
import kotlin.test.assertEquals

class InfiniteScrollTest {
    @Test
    fun `Infinite Scroll of the Datastar Frontend Reactivity`() {
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
                                dataSignals(
                                    "offset" to 10,
                                    "limit" to 5,
                                )
                                text("Agents")
                            }
                            table {
                                thead {
                                    tr {
                                        th { text("Name") }
                                        th { text("Email") }
                                        th { text("ID") }
                                    }
                                }
                                tbody {
                                    // Firt 10 rows (Agent Smith 0-9)
                                    for (i in 0..9) {
                                        tr {
                                            td { text("Agent Smith $i") }
                                            td { text("void$i@null.org") }
                                            td { text(generateId(i)) }
                                        }
                                    }
                                }
                            }
                            div {
                                dataOnIntersect { get(::getMore) }
                                text("Loading...")
                            }
                        }
                    }
                }
            }

    @Path("/examples/infinite_scroll/more")
    private fun getMore() {}

    // Ids from the first 10 rows
    private fun generateId(index: Int): String {
        val ids =
            listOf(
                "1982e3a7bb241055",
                "65cd25028f98f158",
                "7b95a7322f5da314",
                "7324dc1e7e9474f0",
                "628911027fcf803f",
                "5edb980100c87e72",
                "3564a48862bc4a0d",
                "6eed105b82285fa",
                "664f427c6b2c4bea",
                "28353a066812b268",
            )
        return ids.getOrElse(index) { "generated-id-$index" }
    }

    private val expectedDatastarRx = """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
               <div data-signals="{offset: 10, limit: 5}">
                   Agents
               </div>
               <table>
                    <thead>
                        <tr>
                            <th>
                                Name
                            </th>
                            <th>
                                Email
                            </th>
                            <th>
                                ID
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                Agent Smith 0
                            </td>
                            <td>
                                void0@null.org
                            </td>
                            <td>
                                1982e3a7bb241055
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 1
                            </td>
                            <td>
                                void1@null.org
                            </td>
                            <td>
                                65cd25028f98f158
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 2
                            </td>
                            <td>
                                void2@null.org
                            </td>
                            <td>
                                7b95a7322f5da314
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 3
                            </td>
                            <td>
                                void3@null.org
                            </td>
                            <td>
                                7324dc1e7e9474f0
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 4
                            </td>
                            <td>
                                void4@null.org
                            </td>
                            <td>
                                628911027fcf803f
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 5
                            </td>
                            <td>
                                void5@null.org
                            </td>
                            <td>
                                5edb980100c87e72
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 6
                            </td>
                            <td>
                                void6@null.org
                            </td>
                            <td>
                                3564a48862bc4a0d
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 7
                            </td>
                            <td>
                                void7@null.org
                            </td>
                            <td>
                                6eed105b82285fa
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 8
                            </td>
                            <td>
                                void8@null.org
                            </td>
                            <td>
                                664f427c6b2c4bea
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Agent Smith 9
                            </td>
                            <td>
                                void9@null.org
                            </td>
                            <td>
                                28353a066812b268
                            </td>
                        </tr>
                    </tbody>
               </table>
               <div data-on-intersect="@get('/examples/infinite_scroll/more')">
                    Loading...
               </div>
            </body>
        </html>        
        """
}
