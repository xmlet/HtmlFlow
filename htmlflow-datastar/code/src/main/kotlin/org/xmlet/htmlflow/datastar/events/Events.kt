package org.xmlet.htmlflow.datastar.events

import org.xmlet.htmlflow.datastar.expressions.DataStarExpression
import org.xmlet.htmlflow.datastar.expressions.DataStarExpressionOp

sealed class DomEvent(
    val name: String,
    private val ref: String = "evt",
) {
    protected fun prop(name: String) = DataStarExpressionOp("$ref.$name")

    override fun toString() = name
}

sealed class Event(
    name: String,
) : DomEvent(name) {
    val bubbles: DataStarExpression
        get() = prop("bubbles")
    val cancellable: DataStarExpression
        get() = prop("cancellable")
    val composed: DataStarExpression
        get() = prop("composed")
    val currentTarget: DataStarExpression
        get() = prop("currentTarget")
    val defaultPrevented: DataStarExpression
        get() = prop("defaultPrevented")
    val target: DataStarExpression
        get() = prop("target")
    val type: DataStarExpression
        get() = prop("type")
}

sealed class UIEvent(
    name: String,
) : Event(name) {
    val detail: DataStarExpression
        get() = prop("detail")
}

open class InputEvent(
    name: String,
) : UIEvent(name) {
    val data: DataStarExpression
        get() = prop("data")
    val inputType: DataStarExpression
        get() = prop("inputType")
    val isComposing: DataStarExpression
        get() = prop("isComposing")
}

sealed class MouseEvent(
    name: String,
) : UIEvent(name) {
    val altKey: DataStarExpression
        get() = prop("altKey")
    val button: DataStarExpression
        get() = prop("button")
    val buttons: DataStarExpression
        get() = prop("buttons")
    val clientX: DataStarExpression
        get() = prop("clientX")
    val clientY: DataStarExpression
        get() = prop("clientY")
    val ctrlKey: DataStarExpression
        get() = prop("ctrlKey")
    val metaKey: DataStarExpression
        get() = prop("metaKey")
    val movementX: DataStarExpression
        get() = prop("movementX")
    val movementY: DataStarExpression
        get() = prop("movementY")
    val offsetX: DataStarExpression
        get() = prop("offsetX")
    val offsetY: DataStarExpression
        get() = prop("offsetY")
    val pageX: DataStarExpression
        get() = prop("pageX")
    val pageY: DataStarExpression
        get() = prop("pageY")
    val screenX: DataStarExpression
        get() = prop("screenX")
    val screenY: DataStarExpression
        get() = prop("screenY")
    val shiftKey: DataStarExpression
        get() = prop("shiftKey")
    val x: DataStarExpression
        get() = clientX
    val y: DataStarExpression
        get() = clientY
}

sealed class KeyboardEvent(
    name: String,
) : UIEvent(name) {
    val altKey: DataStarExpression
        get() = prop("altKey")
    val code: DataStarExpression
        get() = prop("code")
    val ctrlKey: DataStarExpression
        get() = prop("ctrlKey")
    val isComposing: DataStarExpression
        get() = prop("isComposing")
    val key: DataStarExpression
        get() = prop("key")
    val metaKey: DataStarExpression
        get() = prop("metaKey")
    val repeat: DataStarExpression
        get() = prop("repeat")
    val shiftKey: DataStarExpression
        get() = prop("shiftKey")
}

class SubmitEvent(
    name: String,
) : Event(name) {
    val submitter: DataStarExpression
        get() = prop("submitter")
}

class PointerEvent(
    name: String,
) : MouseEvent(name) {
    val width: DataStarExpression
        get() = prop("width")
    val height: DataStarExpression
        get() = prop("height")
    val twist: DataStarExpression
        get() = prop("twist")
    val pointerType: DataStarExpression
        get() = prop("pointerType")
    val isPrimary: DataStarExpression
        get() = prop("isPrimary")
}

sealed class FocusEvent(
    name: String,
) : UIEvent(name) {
    val relatedTarget: DataStarExpression
        get() = prop("relatedTarget")
}

open class CustomEvent(
    name: String,
) : Event(name) {
    val detail: DataStarExpression
        get() = prop("detail")
}

object Blur : FocusEvent("blur")

object FocusOut : FocusEvent("focusout")

object Focus : FocusEvent("focus")

object FocusIn : FocusEvent("focusin")

object Click : MouseEvent("click")

object Keydown : KeyboardEvent("keydown")

object Input : InputEvent("input")

object Change : InputEvent("change")

object DblClick : MouseEvent("dblclick")
