package htmlflow.datastar

import htmlflow.html
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import kotlin.test.assertEquals

class CustomEventTest {
    @Test
    fun `Custom Event of the Datastar Frontend Reactivity`() {
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
                    p {
                        attrId("foo")
                        val eventDetails = dataSignal("eventDetails")
                        dataOn("myevent", "$eventDetails = evt.detail")
                        dataText("`Last Event Details: ${'$'}{$eventDetails}`")
                    }
                    script {
                        +
                            """
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
                            """.trimIndent()
                    }
                }
            }
        }

    private val expectedDatastarRx =
        """
            <!DOCTYPE html>
        <html>    
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
                </script>
            </head>
            <body>    
                <p id="foo" data-signals-eventDetails="" data-on-myevent="${'$'}eventDetails = evt.detail" data-text="`Last Event Details: ${'$'}{${'$'}eventDetails}`">
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
