package htmlflow.datastar

import htmlflow.html
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.i
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.label
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class InlineValidationTest {
    @Test
    fun `Inline validation of the Datastar Frontend Reactivity`() {
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
                    div {
                        attrId("demo")
                        label {
                            +"Email Address"
                            input {
                                attrType(EnumTypeInputType.EMAIL)
                                attrRequired(true)
                                addAttr("aria-live", "polite")
                                addAttr("aria-describedby", "email-info")
                                dataBind("email")
                                dataOn("keydown", "@post('/examples/inline_validation/validate')") {
                                    debounce(500.milliseconds)
                                }
                            }
                        }
                        p {
                            attrId("email-info")
                            attrClass("info")
                            +"The only valid email address is \"test@test.com\"."
                        }
                        label {
                            +"First Name"
                            input {
                                attrType(EnumTypeInputType.TEXT)
                                attrRequired(true)
                                addAttr("aria-live", "polite")
                                dataBind("first-name")
                                dataOn("keydown", "@post('/examples/inline_validation/validate')") {
                                    debounce(500.milliseconds)
                                }
                            }
                        }
                        label {
                            +"Last Name"
                            input {
                                attrType(EnumTypeInputType.TEXT)
                                attrRequired(true)
                                addAttr("aria-live", "polite")
                                dataBind("last-name")
                                dataOn("keydown", "@post('/examples/inline_validation/validate')") {
                                    debounce(500.milliseconds)
                                }
                            }
                        }
                        button {
                            attrClass("success")
                            dataOn("click", "@post('/examples/inline_validation')")
                            i {
                                attrClass("material-symbols:person-add")
                            }
                            +" Sign Up"
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
                <div id="demo">
                    <label>
                        Email Address
                        <input type="email" required="true" aria-live="polite" aria-describedby="email-info" data-bind-email="" data-on-keydown__debounce.500ms="@post('/examples/inline_validation/validate')">
                    </label>
                    <p id="email-info" class="info">
                        The only valid email address is "test@test.com".
                    </p>
                    <label>
                        First Name
                        <input type="text" required="true" aria-live="polite" data-bind-first-name="" data-on-keydown__debounce.500ms="@post('/examples/inline_validation/validate')">
                    </label>
                    <label>
                        Last Name
                        <input type="text" required="true" aria-live="polite" data-bind-last-name="" data-on-keydown__debounce.500ms="@post('/examples/inline_validation/validate')">
                    </label>
                    <button class="success" data-on-click="@post('/examples/inline_validation')">
                        <i class="material-symbols:person-add">
                        </i>
                        Sign Up
                    </button>
                </div>
            </body>
        </html>
        """
}
