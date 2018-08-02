/*
 * MIT License
 *
 * Copyright (c) 2014-16, Miguel Gamboa (gamboa.pt)
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

package htmlflow;

import java.io.PrintStream;

/**
 * Writes HTML content into a {@link java.io.PrintStream}.
 *
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public interface HtmlWriter<T>{
    /**
     * Writes into an internal PrintStream the HTML content
     * of this element with zero indentation and without a
     * domain object.
     */
    void write();
    /**
     * Writes into an internal PrintStream the HTML content
     * of this element with initial indentation of zero.
     *
     * @param model An optional object model that could be bind to this element.
     */
    default void write(T model) {
        write(0, model);
    }
    /**
     * Writes into an internal PrintStream the HTML content
     * of this element.
     *
     * @param depth The number of tabs indentation.
     * @param model An optional object model that could be bind to this element.
     */
    void write(int depth, T model);

    /**
     * Sets the current PrintStream.
     * @param out
     */
    HtmlWriter<T> setPrintStream(PrintStream out);
}
