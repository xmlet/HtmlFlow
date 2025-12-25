package htmlflow

import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class FormDataTest {
    @Test
    fun `Form Data of the Datastar Frontend Reactivity`() {
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
                    form {
                        attrId("myform")
                        +"foo:"
                        input {
                            attrType(EnumTypeInputType.CHECKBOX)
                            attrName("checkboxes")
                            attrValue("foo")
                        }
                        +"bar:"
                        input {
                            attrType(EnumTypeInputType.CHECKBOX)
                            attrName("checkboxes")
                            attrValue("bar")
                        }
                        +"baz:"
                        input {
                            attrType(EnumTypeInputType.CHECKBOX)
                            attrName("checkboxes")
                            attrValue("baz")
                        }
                        button {
                            dataOn("click","@get('/endpoint', {contentType: 'form'})")
                            +"Submit GET request"
                        }
                        button {
                            dataOn("click","@post('/endpoint', {contentType: 'form'})")
                            +"Submit POST request"
                        }
                    }
                    button {
                        dataOn("click","@get('/endpoint', {contentType: 'form', selector: '#myform'})")
                        +"Submit GET request from outside the form"
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
    <form id="myform">
        foo:
        <input type="checkbox" name="checkboxes" value="foo">
        bar:
        <input type="checkbox" name="checkboxes" value="bar">
        baz:
        <input type="checkbox" name="checkboxes" value="baz">
        <button data-on-click="@get('/endpoint', {contentType: 'form'})">
            Submit GET request
        </button>
        <button data-on-click="@post('/endpoint', {contentType: 'form'})">
            Submit POST request
        </button>
    </form>
    <button data-on-click="@get('/endpoint', {contentType: 'form', selector: '#myform'})">
        Submit GET request from outside the form
    </button>
</body>
</html>
    """

}