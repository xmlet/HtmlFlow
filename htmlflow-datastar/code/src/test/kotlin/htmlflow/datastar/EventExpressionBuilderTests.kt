package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.builders.EventExpressionBuilder
import org.xmlet.htmlflow.datastar.events.Blur
import org.xmlet.htmlflow.datastar.events.Change
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.events.CustomEvent
import org.xmlet.htmlflow.datastar.events.DblClick
import org.xmlet.htmlflow.datastar.events.Event
import org.xmlet.htmlflow.datastar.events.Focus
import org.xmlet.htmlflow.datastar.events.FocusIn
import org.xmlet.htmlflow.datastar.events.FocusOut
import org.xmlet.htmlflow.datastar.events.Input
import org.xmlet.htmlflow.datastar.events.InputEvent
import org.xmlet.htmlflow.datastar.events.Keydown
import org.xmlet.htmlflow.datastar.events.PointerEvent
import org.xmlet.htmlflow.datastar.events.SubmitEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventExpressionBuilderTests {
    @Test
    fun `all events should expose expected names`() {
        with(Blur) { assertEquals("blur", toString()) }
        with(FocusOut) { assertEquals("focusout", toString()) }
        with(Focus) { assertEquals("focus", toString()) }
        with(FocusIn) { assertEquals("focusin", toString()) }
        with(Click) { assertEquals("click", toString()) }
        with(Keydown) { assertEquals("keydown", toString()) }
        with(Input) { assertEquals("input", toString()) }
        with(Change) { assertEquals("change", toString()) }
        with(DblClick) { assertEquals("dblclick", toString()) }

        with(SubmitEvent("submit")) { assertEquals("submit", toString()) }
        with(PointerEvent("pointerdown")) { assertEquals("pointerdown", toString()) }
        with(CustomEvent("custom:done")) { assertEquals("custom:done", toString()) }
        with(InputEvent("beforeinput")) { assertEquals("beforeinput", toString()) }
    }

    @Test
    fun `dataOn should expose base event properties through evt`() {
        val html =
            renderDataOn(Click) {
                with(evt) {
                    bubbles and cancellable and composed and currentTarget and defaultPrevented and target and type
                }
            }

        assertTrue(
            html.contains(
                "data-on:click=\"evt.bubbles && evt.cancellable && evt.composed && " +
                    "evt.currentTarget && evt.defaultPrevented && evt.target && evt.type\"",
            ),
        )
    }

    @Test
    fun `dataOn should expose ui and focus properties through evt`() {
        val html =
            renderDataOn(Focus) {
                with(evt) {
                    detail and relatedTarget
                }
            }

        assertTrue(html.contains("data-on:focus=\"evt.detail && evt.relatedTarget\""))
    }

    @Test
    fun `dataOn should expose input event properties through evt`() {
        val html =
            renderDataOn(Input) {
                with(evt) {
                    detail and data and inputType and isComposing
                }
            }

        assertTrue(html.contains("data-on:input=\"evt.detail && evt.data && evt.inputType && evt.isComposing\""))
    }

    @Test
    fun `dataOn should expose mouse event properties through evt`() {
        val html =
            renderDataOn(DblClick) {
                with(evt) {
                    detail and altKey and button and buttons and clientX and clientY and ctrlKey and metaKey and
                        movementX and movementY and offsetX and offsetY and pageX and pageY and
                        screenX and screenY and shiftKey and x and y
                }
            }

        assertTrue(
            html.contains(
                "data-on:dblclick=\"evt.detail && evt.altKey && evt.button && evt.buttons && " +
                    "evt.clientX && evt.clientY && evt.ctrlKey && evt.metaKey && evt.movementX && " +
                    "evt.movementY && evt.offsetX && evt.offsetY && evt.pageX && evt.pageY && " +
                    "evt.screenX && evt.screenY && evt.shiftKey && evt.clientX && evt.clientY\"",
            ),
        )
    }

    @Test
    fun `dataOn click should expose mouse event properties through evt`() {
        val html =
            renderDataOn(Click) {
                with(evt) {
                    button and clientX and clientY and offsetX and offsetY
                }
            }

        assertTrue(
            html.contains(
                "data-on:click=\"evt.button && evt.clientX && evt.clientY && evt.offsetX && evt.offsetY\"",
            ),
        )
    }

    @Test
    fun `dataOn should expose keyboard event properties through evt`() {
        val html =
            renderDataOn(Keydown) {
                with(evt) {
                    detail and altKey and code and ctrlKey and isComposing and key and metaKey and repeat and shiftKey
                }
            }

        assertTrue(
            html.contains(
                "data-on:keydown=\"evt.detail && evt.altKey && evt.code && evt.ctrlKey && " +
                    "evt.isComposing && evt.key && evt.metaKey && evt.repeat && evt.shiftKey\"",
            ),
        )
    }

    @Test
    fun `dataOn should expose submit event properties through evt`() {
        val html =
            renderDataOn(SubmitEvent("submit")) {
                with(evt) {
                    type and submitter
                }
            }

        assertTrue(html.contains("data-on:submit=\"evt.type && evt.submitter\""))
    }

    @Test
    fun `dataOn should expose pointer event properties through evt`() {
        val html =
            renderDataOn(PointerEvent("pointerdown")) {
                with(evt) {
                    detail and clientX and clientY and width and height and twist and pointerType and isPrimary
                }
            }

        assertTrue(
            html.contains(
                "data-on:pointerdown=\"evt.detail && evt.clientX && evt.clientY && evt.width && " +
                    "evt.height && evt.twist && evt.pointerType && evt.isPrimary\"",
            ),
        )
    }

    @Test
    fun `dataOn should expose custom event properties through evt`() {
        val html =
            renderDataOn(CustomEvent("custom:done")) {
                with(evt) {
                    type and detail
                }
            }

        assertTrue(html.contains("data-on:custom:done=\"evt.type && evt.detail\""))
    }

    @Test
    fun `dataOn should support actions and modifiers with evt access`() {
        val html =
            renderDataOn(Click) {
                with(evt) {
                    target and get(::getUsers)
                }
                modifiers {
                    once()
                    prevent()
                    stop()
                }
            }

        assertTrue(html.contains("data-on:click__once__prevent__stop=\"evt.target && @get('/users')\""))
    }

    private fun <EVT : Event> renderDataOn(
        event: EVT,
        block: EventExpressionBuilder<EVT>.() -> Unit,
    ): String =
        StringBuilder()
            .apply {
                doc {
                    html {
                        div {
                            dataOn(event, block)
                        }
                    }
                }
            }.toString()

    @Path("/users")
    private fun getUsers() {}
}
