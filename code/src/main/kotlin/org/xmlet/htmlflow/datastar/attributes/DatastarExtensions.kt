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

package org.xmlet.htmlflow.datastar.attributes

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlflow.datastar.builders.ExpressionBuilder
import org.xmlet.htmlflow.datastar.expressions.Signal
import org.xmlet.htmlflow.datastar.expressions.SignalPatchFilter
import org.xmlet.htmlflow.datastar.expressions.signal
import org.xmlet.htmlflow.datastar.serialization.JavaScriptLiterals
import org.xmlet.htmlflow.datastar.serialization.Serializer

/**
 * Binds any HTML attribute to an expression, keeping it synchronized.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param name the name of the HTML attribute to set the value to the expression
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(
    name: String,
    block: ExpressionBuilder.() -> Unit,
) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-attr:$name", expression)
}

/**
 * Binds multiple HTML attribute simultaneously through key-value pairs, keeping them synchronized with their expressions.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-attr attribute will be added
 * @param attrs a JavaScript expression that computes the values of multiple attribute on an element using a set of key-value pairs
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataAttr(vararg attrs: Pair<String, Signal<*>>) {
    val serialized =
        JavaScriptLiterals.objectLiteral(
            attrs.map { (name, value) ->
                name to value.toString()
            },
        )
    this.visitor.visitAttribute("data-attr", serialized)
}

/**
 * Executes an expression on page load and whenever any signals in the expression change.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-effect attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataEffect(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-effect", expression)
}

/**
 * Creates a signal and sets its value to true while a fetch request is in flight, otherwise false.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-indicator attribute will be added
 * @param name the name of the indicator signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIndicator(name: String): Signal<Boolean> {
    this.visitor.visitAttribute("data-indicator", name)
    return signal(name)
}

/**
 * Toggles element visibility based on an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-show attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataShow(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-show", expression)
}

/**
 * Binds the text content of an element to an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-text attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-text", expression)
}

/**
 * Filters which signals to watch when using the data-on-signal-patch attribute.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch-filter attribute will be added
 * @param filterBuilder a builder lambda that configures a [SignalPatchFilter]
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOnSignalPatchFilter(filterBuilder: SignalPatchFilter.() -> Unit) {
    val filter = SignalPatchFilter().apply(filterBuilder)
    this.visitor.visitAttribute("data-on-signal-patch-filter", "$filter")
}

/**
 * Tells the PatchElements watcher to skip processing an element and its children when morphing elements.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-on-signal-patch-filter attribute will be added
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataIgnoreMorph() {
    this.visitor.visitAttribute("data-ignore-morph", "")
}

/**
 * Preserves the value of an attribute when morphing DOM elements.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-preserve-attr attribute will be added
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataPreserveAttr(block: ExpressionBuilder.() -> Unit) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-preserve-attr", expression)
}

/**
 * Sets the value of inline CSS styles on an element based on an expression, and keeps them in sync.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-style attribute will be added
 * @param name the name of the CSS property to set the value to the expression
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataStyle(
    name: String,
    block: ExpressionBuilder.() -> Unit,
) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-style:$name", expression)
}

/**
 * Binds multiple CSS style properties simultaneously through key-value pairs, keeping them synchronized with their expressions.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-style attribute will be added
 * @param styles key-value pairs where the key is a CSS property name and the value
 * is a JavaScript expression that computes the style value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataStyle(vararg styles: Pair<String, String>) {
    val serialized = JavaScriptLiterals.objectLiteral(styles.asIterable())
    this.visitor.visitAttribute("data-style", serialized)
}

/**
 * Creates a new signal that is a reference to the element on which the data attribute is placed.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-ref attribute will be added
 * @param name the name of the indicator signal
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataRef(name: String): Signal<Any> {
    this.visitor.visitAttribute("data-ref", name)
    return signal(name)
}

/**
 * Creates a computed signal whose value is derived from an expression
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-computed attribute will be added
 * @param name the name of the signal
 * @param block Block with tha lambda containing the expression
 * @return a Signal instance with the given name
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataComputed(
    name: String,
    block: ExpressionBuilder.() -> Unit,
): Signal<Any> {
    val expression = ExpressionBuilder().apply(block).getExpression()
    val computed = Serializer.serializeComputed(name, expression)
    this.visitor.visitAttribute("data-computed", computed)
    return signal(name)
}

/**
 * Adds or removes a class from an element based on an expression.
 *
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-class attribute will be added
 * @param className the name of the class from the element
 * @param block Block with tha lambda containing the expression
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataClass(
    className: String,
    block: ExpressionBuilder.() -> Unit,
) {
    val expression = ExpressionBuilder().apply(block).getExpression()
    this.visitor.visitAttribute("data-class:$className", expression)
}
