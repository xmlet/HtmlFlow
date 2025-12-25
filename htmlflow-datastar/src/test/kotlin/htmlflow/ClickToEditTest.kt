package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class ClickToEditTest {
    @Test
    fun `Click To Edit of the Datastar Frontend Reactivity`() {
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
                        p{ +"First Name: John" }
                        p{ +"Last Name: Doe" }
                        p{ +"Email: joe@blow.com" }
                        div{
                            button{
                                attrClass("info")
                                val fetching = dataIndicator("fetching")
                                dataAttr("disabled", "$fetching")
                                dataOn("click", "@get('/examples/click_to_edit/edit')")
                                +"Edit"
                            }
                            button{
                                attrClass("warning")
                                val fetching = dataIndicator("fetching")
                                dataAttr("disabled", "$fetching")
                                dataOn("click", "@patch('/examples/click_to_edit/reset')")
                                +"Reset"
                            }
                        }
                    }
                }
            }
        }

    private val expectedDatastarRx ="""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
        </script>
    </head>
<body>
    <div id="demo">
        <p>
            First Name: John
        </p>
        <p>
            Last Name: Doe
        </p>
        <p>
            Email: joe@blow.com
        </p>
        <div>
            <button class="info" data-indicator-fetching="" data-attr-disabled="${'$'}fetching" data-on-click="@get('/examples/click_to_edit/edit')">
                Edit
            </button>
            <button class="warning" data-indicator-fetching="" data-attr-disabled="${'$'}fetching" data-on-click="@patch('/examples/click_to_edit/reset')">
                Reset
            </button>
        </div>
    </div>
</body>
</html>
    """
}