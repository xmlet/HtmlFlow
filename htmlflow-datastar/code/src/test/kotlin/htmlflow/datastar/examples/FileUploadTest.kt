@file:Suppress("ktlint:standard:no-wildcard-imports")

package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlapifaster.*
import org.xmlet.htmlflow.datastar.attributes.dataAttr
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class FileUploadTest {
    @Test
    fun `File Upload of the Datastar Frontend Reactivity`() {
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
                            val files = dataSignal("files")
                            label {
                                p { text("Pick anything less than 1MB") }
                                input {
                                    attrType(EnumTypeInputType.FILE)
                                    dataBind(files.name)
                                    attrMultiple(true)
                                }
                            }
                            button {
                                attrClass("warning")
                                dataOn(Click) {
                                    +"$files.length"
                                    post(::uploadFiles)
                                }
                                dataAttr("aria-disabled") { +$$"`${!$$files.length}`" }
                                text("Submit")
                            }
                            div {
                                attrId("file-upload")
                                attrHidden(true)
                            }
                        }
                    }
                }
            }

    @Path("/examples/file_upload")
    private fun uploadFiles() {}

    private val expectedDatastarRx = $$"""
    <!DOCTYPE html>
<html>
    <head>
        <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
        </script>
    </head>
<body data-signals="{files: ""}">
    <label>
        <p>
            Pick anything less than 1MB
        </p>
        <input type="file" data-bind="files" multiple="multiple">
    </label>
    <button class="warning" data-on:click="$files.length; @post('/examples/file_upload')" data-attr:aria-disabled="`${!$files.length}`">        
        Submit
    </button>
    <div id="file-upload" hidden="hidden">
    </div>
</body>
</html>
    """
}
