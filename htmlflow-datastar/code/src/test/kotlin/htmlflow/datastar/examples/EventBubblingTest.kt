package htmlflow.datastar.examples

import htmlflow.doc
import htmlflow.html
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.br
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlapifaster.style
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Click
import kotlin.test.Test
import kotlin.test.assertEquals

class EventBubblingTest {
    @Test
    fun `Edit Row of the Datastar Frontend Reactivity`() {
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
                                val key = dataSignal("key")
                                text("Key pressed:")
                                span {
                                    dataText { +key }
                                }
                                div {
                                    attrId("button-container")
                                    dataOn(Click) {
                                        +"$key = evt.target.dataset.id"
                                    }
                                    button {
                                        addAttr("data-id", "KEY ELSE")
                                        attrClass("gray")
                                        text("KEY")
                                        br { text("ELSE") }
                                    }
                                    button {
                                        addAttr("data-id", "CM")
                                        text("CM")
                                    }
                                    button {
                                        addAttr("data-id", "OM")
                                        text("OM")
                                    }
                                    button {
                                        addAttr("data-id", "FETCH")
                                        text("FETCH")
                                    }
                                    button {
                                        addAttr("data-id", "SET")
                                        text("SET")
                                    }
                                    button {
                                        addAttr("data-id", "EXEC")
                                        text("EXEC")
                                    }
                                    button {
                                        addAttr("data-id", "TEST ALARM")
                                        attrClass("gray")
                                        text("TEST")
                                        br { text("ALARM") }
                                    }
                                    button {
                                        addAttr("data-id", "3")
                                        text("3")
                                    }
                                    button {
                                        addAttr("data-id", "2")
                                        text("2")
                                    }
                                    button {
                                        addAttr("data-id", "1")
                                        text("1")
                                    }
                                    button {
                                        addAttr("data-id", "ENTER")
                                        text("ENTER")
                                    }
                                    button {
                                        addAttr("data-id", "CLEAR")
                                        text("CLEAR")
                                    }
                                }
                            }
                            style {
                                raw(
                                    """
                                    #button-container {
                                        pointer-events: none;
                                    }
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
                <div id="demo" data-signals="{key: ""}">
                    Key pressed: 
                    <span data-text="$key">
                    </span>
                    <div id="button-container" data-on:click="$key = evt.target.dataset.id">
                        <button data-id="KEY ELSE" class="gray">
                            KEY
                                <br>
                                    ELSE
                        </button>
                        <button data-id="CM">
                            CM
                        </button>
                        <button data-id="OM">
                            OM
                        </button>
                        <button data-id="FETCH">
                            FETCH
                        </button>
                        <button data-id="SET">
                            SET
                        </button>
                        <button data-id="EXEC">
                            EXEC
                        </button>
                        <button data-id="TEST ALARM" class="gray">
                            TEST
                                <br>
                                    ALARM
                        </button>
                        <button data-id="3">
                            3
                        </button>
                        <button data-id="2">
                            2
                        </button>
                        <button data-id="1">
                            1
                        </button>
                        <button data-id="ENTER">
                            ENTER
                        </button>
                        <button data-id="CLEAR">
                            CLEAR
                        </button>
                    </div>
                </div>
                <style>
                    #button-container {
                        pointer-events: none;
                    }
                </style>
            </body>
        </html>
        """
}
