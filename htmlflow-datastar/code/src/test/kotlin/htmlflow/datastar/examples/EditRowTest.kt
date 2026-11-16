package htmlflow.datastar.examples

import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.EnumTypeScriptType
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
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.Signal
import kotlin.test.Test
import kotlin.test.assertEquals

class EditRowTest {
    @Test
    fun `Edit Row of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.render(initialState).toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    // Module-level signal accessible to all functions
    private lateinit var editing: Signal<Boolean>

    private val demoDastarRx =
        view<TableState> {
            html {
                head {
                    script {
                        attrType(EnumTypeScriptType.MODULE)
                        attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                    }
                }
                body {
                    div {
                        hfEditRowTable()
                    }
                }
            }
        }

    fun Div<*>.hfEditRowTable() {
        attrId("demo")
        val editing = dataSignal("_editing", false).also { editing = it }
        table {
            thead {
                tr {
                    th { text("Name") }
                    th { text("Email") }
                    th { text("Actions") }
                }
            }
            tbody {
                dyn { state: TableState ->
                    state.users.forEach { user ->
                        tr {
                            attrId("row-${user.idx}")
                            td { text(user.name) }
                            td { text(user.email) }
                            td {
                                button {
                                    attrId("edit-row-${user.idx}")
                                    dataOn(Click) {
                                        editing.setValue(true)
                                        get("/examples/edit-row/${user.idx}")
                                    }
                                    val fetching = dataIndicator("_fetching")
                                    dataAttr("disabled") { fetching or editing }
                                    text("Edit")
                                }
                            }
                        }
                    }
                }
            }
        }
        div {
            button {
                attrId("reset")
                dataOn(Click) {
                    editing.setValue(false)
                    put(::resetTable)
                }
                val fetching = dataIndicator("_fetching")
                dataAttr("disabled") { +fetching }
                text("Reset")
            }
        }
    }

    private val initialState = TableState(listOf(TableUser(0, "Joe Smith", "joe@smith.org")))

    private data class TableState(
        val users: List<TableUser>,
    )

    private data class TableUser(
        val idx: Int,
        val name: String,
        val email: String,
    )

    @Path("/examples/edit-row/cancel")
    private fun editRowCancel() {}

    @Path("/examples/edit-row/reset")
    private fun resetTable() {}

    private val expectedDatastarRx =
        $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demo" data-signals="{_editing: false}">
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
                                    <button id="edit-row-0" data-on:click="$$editing = true; @get('/examples/edit-row/0')" data-indicator="_fetching" data-attr:disabled="$_fetching || $_editing">
                                        Edit
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>  
                    <div>
                        <button id="reset" data-on:click="$_editing = false; @put('/examples/edit-row/reset')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                            Reset
                        </button>
                    </div>
                </div>
            </body>
        </html>
        """
}
