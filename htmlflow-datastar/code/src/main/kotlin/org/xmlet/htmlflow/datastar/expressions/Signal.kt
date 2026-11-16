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

package org.xmlet.htmlflow.datastar.expressions

/**
 * Represents a Reactive Signal as defined by TC39 proposals.
 *
 * A [Signal] is a static representation of a Reactive Signal used by the data-star
 * processing environment. **Note: This class does not behave as a reactive signal.**
 * It is only a representation that provides metadata about the signal.
 *
 * The value is **not reactive** and will not be updated after initialization.
 *
 * @property name the name that identifies the signal
 */
class Signal<T> internal constructor(
    val name: String,
) : DataStarExpression(makeExpression(name)) {
    init {
        require(
            !name.contains("__"),
        ) { "Signal names cannot begin with nor contain a double underscore, due to its use as a modifier delimiter." }
    }

    override fun toString() = this.syntax
}

/**
 * Creates a public [Signal] instance that can be used by downstream libraries without
 * requiring access to the internal constructor.
 */
fun <T> signal(name: String): Signal<T> = Signal(name)

private fun makeExpression(name: String): String = "$$name"
