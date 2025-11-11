package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class BulkUpdateTest {
    @Test
    fun `BadApple of the Datastar Frontend Reactivity`() {
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
                    div{
                        attrId("demo")
                        val fetching = dataSignal("_fetching", false)
                        val selections = dataSignal("selections", "Array(4).fill(false)")
                        val _all = dataSignal("_all", null) // Needs to be checked
                        table{
                            thead{
                                tr{
                                    th{
                                        input{
                                            attrType(EnumTypeInputType.CHECKBOX)
                                            dataBind("_all", null)
                                            dataOn("change", "$selections = Array(4).fill($_all)")
                                            //dataEffect()
                                            dataAttr("disabled", fetching)
                                        }
                                    }
                                    th{
                                        +"Name"
                                    }
                                    th{
                                        +"Email"
                                    }
                                    th{
                                        +"Status"
                                    }
                                }
                            }
                            tbody{
                                tr{
                                    td{
                                        input{
                                            attrType(EnumTypeInputType.CHECKBOX)
                                            dataBind(selections)
                                            dataAttr("disabled", fetching)


                                        }
                                    }
                                    td{
                                        +"Joe Smith"
                                    }
                                    td{
                                        +"joe@smith.org"
                                    }
                                    td{
                                        +"Active"
                                    }
                                }
                            }
                        }
                        div{
                            button{
                                attrClass("success")
                                dataOn("click", "@put('/examples/bulk_update/activate')")
                                dataIndicator("_fetching", true)
                                dataAttr("disabled", fetching)
                                i{
                                    attrClass("pixelarticons:user-plus")
                                }
                                +"Activate"
                            }
                            button{
                                attrClass("error")
                                dataOn("click", "@put('/examples/bulk_update/deactivate')")
                                dataIndicator("_fetching", true)
                                dataAttr("disabled", fetching)
                                i{
                                    attrClass("pixelarticons:user-x")
                                }
                                +"Deactivate"
                            }
                        }
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
    <div id="demo" data-signals-_fetching="false" data-signals-selections="'Array(4).fill(false)'" data-signals-_all="">
        <table>
            <thead>
                <tr>
                    <th>
                        <input type="checkbox" data-bind-_all="" data-on-change="${'$'}selections = Array(4).fill(${'$'}_all)" data-attr-disabled="${'$'}_fetching">
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
                        <input type="checkbox" data-bind-selections="" data-attr-disabled="${'$'}_fetching">
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
            <button class="success" data-on-click="@put('/examples/bulk_update/activate')" data-indicator-_fetching="" data-attr-disabled="${'$'}_fetching">
                <i class="pixelarticons:user-plus">
                </i>
                Activate
            </button>
            <button class="error" data-on-click="@put('/examples/bulk_update/deactivate')" data-indicator-_fetching="" data-attr-disabled="${'$'}_fetching">
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