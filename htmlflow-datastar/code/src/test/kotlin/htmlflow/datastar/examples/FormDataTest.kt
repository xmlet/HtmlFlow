@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.expressions.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals

class FormDataTest {
    @Test
    fun `Form Data of the Datastar Frontend Reactivity`() {
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
                            form {
                                attrId("myform")
                                text("foo:")
                                input {
                                    attrType(EnumTypeInputType.CHECKBOX)
                                    attrName("checkboxes")
                                    attrValue("foo")
                                }
                                text("bar:")
                                input {
                                    attrType(EnumTypeInputType.CHECKBOX)
                                    attrName("checkboxes")
                                    attrValue("bar")
                                }
                                text("baz:")
                                input {
                                    attrType(EnumTypeInputType.CHECKBOX)
                                    attrName("checkboxes")
                                    attrValue("baz")
                                }
                                button {
                                    dataOn(Click) {
                                        get(::endpoint) { contentType = ContentType.FORM }
                                    }
                                    text("Submit GET request")
                                }
                                button {
                                    dataOn(Click) {
                                        post(::endpoint) { contentType = ContentType.FORM }
                                    }
                                    text("Submit POST request")
                                }
                            }
                            button {
                                dataOn(Click) {
                                    get(::endpoint) {
                                        contentType = ContentType.FORM
                                        selector = "#myform"
                                    }
                                }
                                text("Submit GET request from outside the form")
                            }
                        }
                    }
                }
            }

    @Path("/endpoint")
    private fun endpoint() {}

    private val expectedDatastarRx = """
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
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
        <button data-on:click="@get('/endpoint', {contentType: 'form'})">
            Submit GET request
        </button>
        <button data-on:click="@post('/endpoint', {contentType: 'form'})">
            Submit POST request
        </button>
    </form>
    <button data-on:click="@get('/endpoint', {contentType: 'form', selector: '#myform'})">
        Submit GET request from outside the form
    </button>
</body>
</html>
    """
}
