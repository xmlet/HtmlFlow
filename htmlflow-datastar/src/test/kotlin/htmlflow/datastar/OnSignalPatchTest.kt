package htmlflow.datastar

import htmlflow.html
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.h3
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.pre
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import kotlin.test.assertEquals

class OnSignalPatchTest {
    @Test
    fun `On signal patch of the Datastar Frontend Reactivity`() {
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
                        val (counter, message, allChanges, counterChanges) =
                            dataSignals(
                                "counter" to 0,
                                "message" to "Hello World",
                                "allChanges" to emptyList<Any>(),
                                "counterChanges" to emptyList<Any>(),
                            )

                        div {
                            attrClass("actions")
                            button {
                                dataOn("click", "$message = `Updated: ${'$'}{performance.now().toFixed(2)}`")
                                +"Update Message"
                            }
                            button {
                                dataOn("click", "$counter++")
                                +"Increment Counter"
                            }
                            button {
                                attrClass("error")
                                dataOn("click", "$allChanges.length = 0; $counterChanges.length = 0")
                                +"Clear All Changes"
                            }
                        }
                        div {
                            h3 { +"Current Values" }
                            p {
                                +"Counter: "
                                span { dataText("$counter") }
                            }
                            p {
                                +"Message: "
                                span { dataText("$message") }
                            }
                        }
                        div {
                            dataOnSignalPatch("$counterChanges.push(patch)")
                            dataOnSignalPatchFilter("{include: /^counter$/}")
                            h3 { +"Counter Changes Only" }
                            pre {
                                dataJsonSignals("{include: /^counterChanges/}") {
                                    terse()
                                }
                            }
                        }
                        div {
                            dataOnSignalPatch("$allChanges.push(patch)")
                            dataOnSignalPatchFilter("{exclude: /allChanges|counterChanges/}")
                            h3 { +"All Signal Changes" }
                            pre {
                                dataJsonSignals("{include: /^allChanges/}") {
                                    terse()
                                }
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
                <div data-signals="{counter: 0, message: 'Hello World', allChanges: [], counterChanges: []}">
                    <div class="actions">
                        <button data-on-click="${'$'}message = `Updated: ${'$'}{performance.now().toFixed(2)}`">
                            Update Message
                        </button>
                        <button data-on-click="${'$'}counter++">
                            Increment Counter
                        </button>
                        <button class="error" data-on-click="${'$'}allChanges.length = 0; ${'$'}counterChanges.length = 0">
                            Clear All Changes
                        </button>
                    </div>
                    <div>
                        <h3>
                            Current Values
                        </h3>
                        <p>
                        Counter: 
                            <span data-text="${'$'}counter">
                            </span>    
                        </p>
                        <p>
                        Message: 
                            <span data-text="${'$'}message">
                            </span>
                        </p>
                    </div>
                    <div data-on-signal-patch="${'$'}counterChanges.push(patch)" data-on-signal-patch-filter="{include: /^counter$/}">
                        <h3>
                            Counter Changes Only
                        </h3>
                        <pre data-json-signals__terse="{include: /^counterChanges/}">
                        </pre>
                    </div>
                    <div data-on-signal-patch="${'$'}allChanges.push(patch)" data-on-signal-patch-filter="{exclude: /allChanges|counterChanges/}">
                        <h3>
                            All Signal Changes
                        </h3>
                        <pre data-json-signals__terse="{include: /^allChanges/}">
                        </pre>
                    </div>
                </div>
            </body>
        </html>        
        """
}
