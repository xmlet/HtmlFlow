/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow

import htmlflow.datastar.modifiers.attributes.DataComputedModifiers
import htmlflow.datastar.modifiers.attributes.DataInitModifiers
import htmlflow.datastar.modifiers.attributes.DataJsonSignalsModifiers
import htmlflow.datastar.modifiers.attributes.DataOnModifiers
import htmlflow.datastar.modifiers.attributes.DataSignalModifiers
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.Input
import org.xmlet.htmlapifaster.Select
import org.xmlet.htmlapifaster.Textarea

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param value the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R?,
    modifiers: String = "",
): Signal<R> {
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }

    this.visitor.visitAttribute("data-signals-$name$modifiers", res)
    return Signal(name, value)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @return a list of Signal instances with the given names and values
 */
fun <E : Element<*, *>, P : Element<*, *>, Any> Element<E, P>.dataSignals(vararg signals: Pair<String, Any?>): List<Signal<Any>> {
    signals
        .joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
            val res =
                when (value) {
                    is String -> "'$value'"
                    null -> ""
                    else -> "$value"
                }
            "$name: $res"
        }.also {
            this.visitor.visitAttribute("data-signals", it)
        }
    return signals.map { (name, value) -> Signal(name, value) }
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signals pairs of signal names and their corresponding values
 * @return a list of Signal instances with the given names and values
 */
fun <E : Element<*, *>, P : Element<*, *>, Any> Element<E, P>.dataSignals(
    vararg signals: Pair<String, Any?>,
    modifiers: DataSignalModifiers.() -> Unit,
): List<Signal<Any>> {
    signals
        .joinToString(prefix = "{", postfix = "}", separator = ", ") { (name, value) ->
            val res =
                when (value) {
                    is String -> "'$value'"
                    null -> ""
                    else -> "$value"
                }
            "$name: $res"
        }.also {
            val mods = DataSignalModifiers().apply(modifiers).toString()
            this.visitor.visitAttribute("data-signals$mods", it)
        }
    return signals.map { (name, value) -> Signal(name, value) }
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param value the value of the signal
 * @param modifiers that apply to the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataSignal(
    name: String,
    value: R?,
    modifiers: DataSignalModifiers.() -> Unit,
): Signal<R> {
    val mods = DataSignalModifiers().apply(modifiers).toString()
    return dataSignal(name, value, mods)
}

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(name: String): Signal<Any> = dataSignal(name, null)

/**
 * Creates a data signal without an initial value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataSignal(
    name: String,
    modifiers: DataSignalModifiers.() -> Unit,
): Signal<Any> = dataSignal(name, null, modifiers)

/**
 * Creates a signal (if one doesn’t already exist)
 * and sets up two-way data binding between it and an element’s value.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-bind attribute will be added
 * @param name the name of the signal to bind
 * @param value of element to initialize the signal with
 * @return a Signal instance with the given name and value if exists
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataBind(
    name: String,
    value: Any? = null,
): Signal<Any> {
    require(this is Input || this is Select || this is Textarea)
    { "Element must be input, select or text area" }
    this.visitor.visitAttribute("data-bind-$name", "")
    return Signal(name, value)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @param R type of the value of the signal
 * @receiver the Element to which the data-bind attribute will be added
 * @param signal the Signal to bind to (its value takes precedence)
 * @return the same Signal instance
 */
fun <E : Element<*, *>, P : Element<*, *>, R> Element<E, P>.dataBind(signal: Signal<R>): Signal<R> {
    require(this is Input || this is Select || this is Textarea)
    { "Element must be input, select or text area" }
    this.visitor.visitAttribute("data-bind-${signal.name}", "")
    return signal
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(js: String) {
    this.visitor.visitAttribute("data-init", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-init attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataInit(
    js: String,
    modifiers: DataInitModifiers.() -> Unit,
) {
    val mods = DataInitModifiers().apply(modifiers)
    this.visitor.visitAttribute("data-init$mods", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param name the name of the HTML attribute to set the value to the expression
 * @param js the javascript expression that the attribute value will be set to
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(
    name: String,
    js: String,
) {
    if (name.startsWith("data-")) { // Improve this validation to support custom elements
        this.visitor.visitAttribute("data-attr:$name", js)
    } else {
        this.visitor.visitAttribute("data-attr-$name", js)
    }
}

/**
 * Not finished yet
 *  data-attr="{title: $foo, disabled: $bar}
 *   -> data-attr-title="$foo"
 *   -> data-attr-disabled="$bar"
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param js a JavaScript expression that computes the values of multiple attributes on an element using a set of key-value pairs
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(js: String) {
    this.visitor.visitAttribute("data-attr", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-indicator attribute will be added
 * @param name the name of the indicator signal
 * @return a Signal instance with the value true
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIndicator(name: String): Signal<Boolean> {
    this.visitor.visitAttribute("data-indicator-$name", "")
    return Signal(name, true)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    js: String,
    modifiers: String = "",
): Signal<Any> {
    this.visitor.visitAttribute("data-computed-$name$modifiers", js)
    return Signal(name)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param name the name of the signal
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    js: String,
    modifiers: DataComputedModifiers.() -> Unit,
): Signal<Any> {
    val mods = DataComputedModifiers().apply(modifiers).toString()
    return dataComputed(name, js, mods)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    js: String,
    modifiers: String = "",
) {
    this.visitor.visitAttribute("data-on-$event$modifiers", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    js: String,
    modifiers: DataOnModifiers.() -> Unit,
) {
    val mods = DataOnModifiers().apply(modifiers).toString()
    return dataOn(event, js, mods)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(js: String) {
    this.visitor.visitAttribute("data-show", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signal the Signal whose value will be used for the attribute
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(signal: Signal<*>) {
    this.visitor.visitAttribute("data-show", signal.toString())
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(js: String) {
    this.visitor.visitAttribute("data-text", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param signal the Signal whose value will be used for the attribute
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(signal: Signal<*>) {
    this.visitor.visitAttribute("data-text", signal.toString())
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that is run when the element intersects the viewport
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnIntersect(js: String) {
    this.visitor.visitAttribute("data-on-intersect", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param js a JavaScript expression that is run when any signal is patched
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatch(js: String) {
    this.visitor.visitAttribute("data-on-signal-patch", js)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatchFilter(jsObj: String) {
    this.visitor.visitAttribute("data-on-signal-patch-filter", jsObj)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(jsObj: String = "") {
    this.visitor.visitAttribute("data-json-signals", jsObj)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param jsObj a JavaScript object with include and/or exclude properties that are regular expressions, that filter which signals to watch.
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataJsonSignals(
    jsObj: String = "",
    mods: DataJsonSignalsModifiers.() -> Unit,
) {
    val mods = DataJsonSignalsModifiers().apply(mods).toString()
    this.visitor.visitAttribute("data-json-signals$mods", jsObj)
}
