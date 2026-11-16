@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataEffect
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.events.Change
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.serialization.asRawJS
import kotlin.test.Test
import kotlin.test.assertEquals

class BulkUpdateTest {
    @Test
    fun `BulkUpdate of the Datastar Frontend Reactivity`() {
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
                                attrId("demo")
                                val (fetching, selections) =
                                    dataSignals(
                                        "_fetching" to false,
                                        "selections" to "Array(4).fill(false)".asRawJS(),
                                    ) { modifiers { ifMissing() } }
                                buildTable(fetching, selections)
                                div {
                                    button {
                                        attrClass("success")
                                        dataOn(Click) {
                                            put(::activate)
                                        }
                                        dataIndicator(fetching.name)
                                        dataAttr("disabled") { +fetching }
                                        i { attrClass("pixelarticons:user-plus") }
                                        text("Activate")
                                    }
                                    button {
                                        attrClass("error")
                                        dataOn(Click) {
                                            put(::deactivate)
                                        }
                                        dataIndicator(fetching.name)
                                        dataAttr("disabled") { +fetching }
                                        i { attrClass("pixelarticons:user-x") }
                                        text("Deactivate")
                                    }
                                }
                            }
                        }
                    }
                }
            }

    fun Div<*>.buildTable(
        fetching: Signal<*>,
        selections: Signal<*>,
    ) {
        table {
            thead {
                tr {
                    th {
                        input {
                            attrType(EnumTypeInputType.CHECKBOX)
                            dataOn(Change) {
                                setAll("el.checked") { include = Regex("^selections") }
                            }
                            dataEffect { +$$"el.checked = $selections.every(Boolean)" }
                            dataAttr("disabled") { +fetching }
                        }
                    }
                    th { text("Name") }
                    th { text("Email") }
                    th { text("Status") }
                }
            }
            tbody {
                tr {
                    td {
                        input {
                            attrType(EnumTypeInputType.CHECKBOX)
                            dataBind(selections.name)
                            dataAttr("disabled") { +fetching }
                        }
                    }
                    td { text("Joe Smith") }
                    td { text("joe@smith.org") }
                    td { text("Active") }
                }
            }
        }
    }

    @Path("/bulk-update/activate")
    private fun activate() {
    }

    @Path("/bulk-update/deactivate")
    private fun deactivate() {
    }

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo" data-signals__ifmissing="{_fetching: false, selections: Array(4).fill(false)}">
        <table>
            <thead>
                <tr>
                    <th>
                        <input type="checkbox" data-on:change="@setAll(el.checked, {include: /^selections/})" data-effect="el.checked = $selections.every(Boolean)" data-attr:disabled="$_fetching">
                        </th>
                    <th>
                        Name
                    </th>
                    <th>
                        Email
                    </th>
                    <th>
                        Status
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>
                        <input type="checkbox" data-bind="selections" data-attr:disabled="$_fetching">
                    </td>
                    <td>
                        Joe Smith
                    </td>
                    <td>
                        joe@smith.org
                    </td>
                    <td>
                        Active
                    </td>
                </tr>
            </tbody>
        </table>
        <div>
            <button class="success" data-on:click="@put('/bulk-update/activate')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                <i class="pixelarticons:user-plus">
                </i>
                Activate
            </button>
            <button class="error" data-on:click="@put('/bulk-update/deactivate')" data-indicator="_fetching" data-attr:disabled="$_fetching">
                <i class="pixelarticons:user-x">
                </i>
                Deactivate
            </button>
        </div>
    </div>
</body>
</html>
    """
}
