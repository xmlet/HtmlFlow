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
     * Writes into an internal PrintStream the HTML content.
     */
    void write();
    /**
     * Writes into an internal PrintStream the HTML content
     * binding the object model with the HTML elements.
     *
     * @param model An object model that could be bind to each element.
     */
    void write(T model);

    /**
     * Sets the current PrintStream.
     */
    HtmlWriter<T> setPrintStream(PrintStream out);

    /**
     * Returns a new String with the HTML content of this view.
     */
    String render();

    /**
     * Returns a new String with the HTML content of this view.
     *
     * @param model An object model that could be bind to each element of the view.
     */
    String render(T model);
}
