package htmlflow.datastar

import htmlflow.html
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.*
import kotlin.test.assertEquals

class FileUploadTest {
    @Test
    fun `File Upload of the Datastar Frontend Reactivity`() {
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
                    label {
                        p { +"Pick anything less than 1MB" }
                        input {
                            attrType(EnumTypeInputType.FILE)
                            dataBind("files multiple")
                        }
                    }
                    button {
                        attrClass("warning")
                        val files = dataSignal("files")
                        dataOn("click", "$files.length && @post('/examples/file_upload')")
                        dataAttr("aria-disabled", "`${'$'}{!$files.length}`")
                        +"Submit"
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
    <label>
        <p>
            Pick anything less than 1MB
        </p>
        <input type="file" data-bind-files multiple="">
    </label>
    <button class="warning" data-signals-files="" data-on-click="${'$'}files.length && @post('/examples/file_upload')" data-attr-aria-disabled="`${'$'}{!${'$'}files.length}`">
        Submit
    </button>
</body>
</html>
    """
}
