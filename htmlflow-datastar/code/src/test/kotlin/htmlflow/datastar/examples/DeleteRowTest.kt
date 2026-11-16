package htmlflow.datastar.examples

import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteRowTest {
    @Test
    fun `Delete Row of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.render(users).toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<DeleteRowsState> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        attrId("users-table")
                        hfDeleteRowTable()
                    }
                }
            }
        }

    fun Div<*>.hfDeleteRowTable() {
        table {
            thead {
                tr {
                    th { text("Name") }
                    th { text("Email") }
                    th { text("Actions") }
                }
            }
            tbody {
                dyn { state: DeleteRowsState ->
                    state.users.forEachIndexed { index, user ->
                        hfTableRow(index, user)
                    }
                }
            }
        }
        div {
            button {
                dataOn(Click) {
                    patch(::restoreRows)
                }
                val fetching = dataIndicator("_fetching")
                dataAttr("disabled") { +fetching }
                text("Reset")
            }
        }
    }

    private fun Tbody<*>.hfTableRow(
        index: Int,
        user: TableUser,
    ) = tr {
        attrId("row-$index")
        td { text(user.name) }
        td { text(user.email) }
        td {
            button {
                attrClass("error")
                dataOn(Click) {
                    "confirm('Are you sure?')" and delete("/examples/delete-row/$index")
                }
                val fetching = dataIndicator("_fetching")
                dataAttr("disabled") { +fetching }
                text("Delete")
            }
        }
    }

    private data class TableUser(
        val id: Int,
        val name: String,
        val email: String,
    )

    private data class DeleteRowsState(
        val users: List<TableUser>,
    )

    private val users =
        DeleteRowsState(
            listOf(
                TableUser(0, "Joe Smith", "joe@smith.org"),
                TableUser(1, "Angie MacDowell", "angie@macdowell.org"),
                TableUser(2, "Fuqua Tarkenton", "fuqua@tarkenton.org"),
                TableUser(3, "Kim Yee", "kim@yee.org"),
            ),
        )

    @Path("/examples/delete-row/reset")
    private fun restoreRows() {}

    private val expectedDatastarRx =
        $$"""
         <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="users-table">
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
                            <tr id="row-0">
                                <td>
                                    Joe Smith
                                </td>
                                <td>
                                    joe@smith.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete-row/0')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr id="row-1">
                                <td>
                                    Angie MacDowell
                                </td>
                                <td>
                                    angie@macdowell.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete-row/1')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr id="row-2">
                                <td>
                                    Fuqua Tarkenton
                                </td>
                                <td>
                                    fuqua@tarkenton.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete-row/2')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                            <tr id="row-3">
                                <td>
                                    Kim Yee
                                </td>
                                <td>
                                    kim@yee.org
                                </td>
                                <td>
                                    <button class="error" data-on:click="confirm('Are you sure?') && @delete('/examples/delete-row/3')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div>
                        <button data-on:click="@patch('/examples/delete-row/reset')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                            Reset
                        </button>
                    </div>
                </div>
            </body>
        </html>
        """
}
