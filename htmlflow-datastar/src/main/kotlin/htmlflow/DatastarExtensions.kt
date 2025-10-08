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

import org.xmlet.htmlapifaster.Element

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
): Signal<R> {
    val res =
        when (value) {
            is String -> "'$value'"
            null -> ""
            else -> "$value"
        }
    this.visitor.visitAttribute("data-signals-$name", res)
    return Signal(name, value)
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
): Signal<Any> {
    this.visitor.visitAttribute("data-computed-$name", js)
    return Signal(name)
}

/**
 * @param E type of the Element receiver
 * @param P type of the parent Element of the receiver
 * @receiver the Element to which the data-signal attribute will be added
 * @param event the event to handle
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataOn(
    event: String,
    js: String,
) {
    this.visitor.visitAttribute("data-on-$event", js)
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
 * @param js a JavaScript expression that computes the value of the signal
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
 * @param js a JavaScript expression that computes the value of the signal
 * @return a Signal instance with the given name and value
 */
fun <E : Element<*, *>, P : Element<*, *>> Element<E, P>.dataText(signal: Signal<*>) {
    this.visitor.visitAttribute("data-text", signal.toString())
}
