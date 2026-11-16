package htmlflow.datastar.examples

import htmlflow.div
import htmlflow.dyn
import htmlflow.html
import htmlflow.view
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.table
import org.xmlet.htmlapifaster.tbody
import org.xmlet.htmlapifaster.td
import org.xmlet.htmlapifaster.th
import org.xmlet.htmlapifaster.thead
import org.xmlet.htmlapifaster.tr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Input
import kotlin.collections.forEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class ActiveSearchTest {
    @Test
    fun `Active Search of the Datastar Frontend Reactivity`() {
        val out = demoDastarRx.render(initialContacts)
        val expected = expectedDatastarRx.trimIndent().lines().iterator()
        out.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    private val demoDastarRx =
        view<List<Contact>> {
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
                        input {
                            attrType(EnumTypeInputType.TEXT)
                            attrPlaceholder("Search...")
                            dataBind("search")
                            dataOn(Input) {
                                get(::search)
                                modifiers { debounce(200.milliseconds) }
                            }
                        }
                        table {
                            thead {
                                tr {
                                    th { text("First Name") }
                                    th { text("Last Name") }
                                }
                            }
                            tbody {
                                attrId("contacts")
                                hfContactRows()
                            }
                        }
                    }
                }
            }
        }

    fun Tbody<*>.hfContactRows() {
        dyn { contacts: List<Contact> ->
            contacts.forEach { cnt ->
                tr {
                    td { text(cnt.firstName) }
                    td { text(cnt.lastName) }
                }
            }
        }
    }

    private data class Contact(
        val firstName: String,
        val lastName: String,
    )

    private val initialContacts =
        listOf(
            Contact("Abraham", "Altenwerth"),
            Contact("Adan", "Padberg"),
            Contact("Aiden", "Haley"),
            Contact("Alec", "Kris"),
            Contact("Alfredo", "Nitzsche"),
            Contact("Alisha", "Rogahn"),
            Contact("Alvah", "Bins"),
            Contact("Anabel", "Lehner"),
            Contact("Angela", "Swift"),
            Contact("Annamarie", "Rippin"),
        )

    @Path("/active-search/search")
    private fun search() {}

    private val expectedDatastarRx = """
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo">
        <input type="text" placeholder="Search..." data-bind="search" data-on:input__debounce.200ms="@get('/active-search/search')">
        <table>   
            <thead>
                <tr>
                    <th>
                        First Name
                    </th>
                    <th>
                        Last Name
                    </th>
                </tr>
            </thead> 
            <tbody id="contacts">
                <tr>
                    <td>
                        Abraham
                    </td>
                    <td>
                        Altenwerth
                    </td>
                </tr>
                <tr>
                    <td>
                        Adan
                    </td>
                    <td>
                        Padberg
                    </td>
                </tr>
                <tr>
                    <td>
                        Aiden
                    </td>
                    <td>
                        Haley
                    </td>
                </tr>
                <tr>
                    <td>
                        Alec
                    </td>
                    <td>
                        Kris
                    </td>
                </tr>
                <tr>
                    <td>
                        Alfredo
                    </td>
                    <td>
                        Nitzsche
                    </td>
                </tr>
                <tr>
                    <td>
                        Alisha
                    </td>
                    <td>
                        Rogahn
                    </td>
                </tr>
                <tr>
                    <td>
                        Alvah
                    </td>
                    <td>
                        Bins
                    </td>
                </tr>
                <tr>
                    <td>
                        Anabel
                    </td>
                    <td>
                        Lehner
                    </td>
                </tr>
                <tr>
                    <td>
                        Angela
                    </td>
                    <td>
                        Swift
                    </td>
                </tr>
                <tr>
                    <td>
                        Annamarie
                    </td>
                    <td>
                        Rippin
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
    """
}
