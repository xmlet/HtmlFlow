package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
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
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.events.Keydown
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class InlineValidationTest {
    @Test
    fun `Inline validation of the Datastar Frontend Reactivity`() {
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
                                label {
                                    text("Email Address")
                                    input {
                                        attrType(EnumTypeInputType.EMAIL)
                                        attrRequired(true)
                                        addAttr("aria-live", "polite")
                                        addAttr("aria-describedby", "email-info")
                                        dataBind("email")
                                        dataOn(Keydown) {
                                            post(::validateInline)
                                            modifiers { debounce(500.milliseconds) }
                                        }
                                    }
                                }
                                p {
                                    attrId("email-info")
                                    attrClass("info")
                                    raw("The only valid email address is \"test@test.com\".")
                                }
                                label {
                                    text("First Name")
                                    input {
                                        attrType(EnumTypeInputType.TEXT)
                                        attrRequired(true)
                                        addAttr("aria-live", "polite")
                                        dataBind("first-name")
                                        dataOn(Keydown) {
                                            post(::validateInline)
                                            modifiers { debounce(500.milliseconds) }
                                        }
                                    }
                                }
                                label {
                                    text("Last Name")
                                    input {
                                        attrType(EnumTypeInputType.TEXT)
                                        attrRequired(true)
                                        addAttr("aria-live", "polite")
                                        dataBind("last-name")
                                        dataOn(Keydown) {
                                            post(::validateInline)
                                            modifiers { debounce(500.milliseconds) }
                                        }
                                    }
                                }
                                button {
                                    attrClass("success")
                                    dataOn(Click) {
                                        post(::submitForm)
                                    }
                                    i {
                                        attrClass("material-symbols:person-add")
                                    }
                                    text(" Sign Up")
                                }
                            }
                        }
                    }
                }
            }

    @Path("/examples/inline_validation/validate")
    private fun validateInline() {}

    @Path("/examples/inline_validation")
    private fun submitForm() {}

    private val expectedDatastarRx = """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demo">
                    <label>
                        Email Address
                        <input type="email" required="required" aria-live="polite" aria-describedby="email-info" data-bind="email" data-on:keydown__debounce.500ms="@post('/examples/inline_validation/validate')">
                    </label>
                    <p id="email-info" class="info">
                        The only valid email address is "test@test.com".
                    </p>
                    <label>
                        First Name
                        <input type="text" required="required" aria-live="polite" data-bind="first-name" data-on:keydown__debounce.500ms="@post('/examples/inline_validation/validate')">
                    </label>
                    <label>
                        Last Name
                        <input type="text" required="required" aria-live="polite" data-bind="last-name" data-on:keydown__debounce.500ms="@post('/examples/inline_validation/validate')">
                    </label>
                    <button class="success" data-on:click="@post('/examples/inline_validation')">
                        <i class="material-symbols:person-add">
                        </i>
                        Sign Up
                    </button>
                </div>
            </body>
        </html>
        """
}
