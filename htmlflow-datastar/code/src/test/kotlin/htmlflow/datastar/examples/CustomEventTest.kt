package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.CustomEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class CustomEventTest {
    @Test
    fun `Custom Event of the Datastar Frontend Reactivity`() {
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
                            val eventName = "myevent"
                            p {
                                attrId("foo")
                                val eventDetails = dataSignal("eventDetails")
                                val myEvent = CustomEvent(eventName)
                                dataOn(myEvent) {
                                    eventDetails.setValue(evt.detail)
                                }
                                dataText { +$$"`Last Event Details: ${$$eventDetails}`" }
                            }
                            script {
                                raw(
                                    """
                                    const foo = document.getElementById("foo");
                                    setInterval(() => {
                                        foo.dispatchEvent(
                                            new CustomEvent("$eventName", {
                                                detail: JSON.stringify({
                                                    eventTime: new Date().toLocaleTimeString(),
                                                }),
                                            })
                                        );
                                    }, 1000);
                                    """.trimIndent(),
                                )
                            }
                        }
                    }
                }
            }

    private val expectedDatastarRx =
        $$"""
            <!DOCTYPE html>
        <html>    
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>    
                <p id="foo" data-signals="{eventDetails: ""}" data-on:myevent="$eventDetails = evt.detail" data-text="`Last Event Details: ${$eventDetails}`">
                </p>
                <script>
                    const foo = document.getElementById("foo");
                    setInterval(() => {
                        foo.dispatchEvent(
                            new CustomEvent("myevent", {
                                    detail: JSON.stringify({
                                        eventTime: new Date().toLocaleTimeString(),
                                    }),
                            })
                        );
                    }, 1000);
                </script>
            </body>
        </html>
        
"""
}
