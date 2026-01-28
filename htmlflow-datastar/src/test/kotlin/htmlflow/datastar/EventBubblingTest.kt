package htmlflow.datastar

import htmlflow.html
import htmlflow.view
import org.junit.Test
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.br
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.div
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlapifaster.style
import kotlin.test.assertEquals

class EventBubblingTest {
    @Test
    fun `Edit Row of the Datastar Frontend Reactivity`() {
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
                        val key = dataSignal("key")
                        +"Key pressed:"
                        span {
                            dataText("$key")
                        }
                        div {
                            attrId("button-container")
                            dataOn("click", "$key = evt.target.dataset.id")
                            button {
                                addAttr("data-id", "KEY ELSE")
                                attrClass("gray")
                                +"KEY"
                                br { +"ELSE" }
                            }
                            button {
                                addAttr("data-id", "CM")
                                +"CM"
                            }
                            button {
                                addAttr("data-id", "OM")
                                +"OM"
                            }
                            button {
                                addAttr("data-id", "FETCH")
                                +"FETCH"
                            }
                            button {
                                addAttr("data-id", "SET")
                                +"SET"
                            }
                            button {
                                addAttr("data-id", "EXEC")
                                +"EXEC"
                            }
                            button {
                                addAttr("data-id", "TEST ALARM")
                                attrClass("gray")
                                +"TEST"
                                br { +"ALARM" }
                            }
                            button {
                                addAttr("data-id", "3")
                                +"3"
                            }
                            button {
                                addAttr("data-id", "2")
                                +"2"
                            }
                            button {
                                addAttr("data-id", "1")
                                +"1"
                            }
                            button {
                                addAttr("data-id", "ENTER")
                                +"ENTER"
                            }
                            button {
                                addAttr("data-id", "CLEAR")
                                +"CLEAR"
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

    private val expectedDatastarRx =
        """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.5/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demo" data-signals-key="">
                    Key pressed: 
                    <span data-text="${'$'}key">
                    </span>
                    <div id="button-container" data-on-click="${'$'}key = evt.target.dataset.id">
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
