package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import kotlin.test.assertEquals

class DeleteRowTest {
    @Test
    fun `Delete Row of the Datastar Frontend Reactivity`() {
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
                    table {
                        thead {
                            tr {
                                th { +"Name" }
                                th { +"Email" }
                                th { +"Actions" }
                            }
                        }
                        tbody {
                            tr {
                                td { +"Joe Smith" }
                                td { +"joe@smith.org" }
                                td {
                                    button {
                                        attrClass("error")
                                        dataOn("click", "confirm('Are you sure?') && @delete('/examples/delete_row/0')")
                                        val fetching = dataIndicator("fetching")
                                        dataAttr("disabled", "$fetching")
                                        +"Delete"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    private val expectedDatastarRx =
        """
         <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
                </script>
            </head>
            <body>
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
                                Actions
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                Joe Smith
                            </td>
                            <td>
                                joe@smith.org
                            </td>
                            <td>
                                <button class="error" data-on-click="confirm('Are you sure?') && @delete('/examples/delete_row/0')" data-indicator-fetching="" data-attr-disabled="${'$'}fetching">
                                    Delete
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>   
            </body>
        </html>
        """
}
