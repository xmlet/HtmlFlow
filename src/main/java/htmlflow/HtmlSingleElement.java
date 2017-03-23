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

import htmlflow.attribute.Attribute;

import java.io.PrintStream;

/**
 * HtmlSingleElement represents an {@link HtmlElement} without children,
 * such as img, br, or hr.
 * Thus, for now,  the model {@code T} is useless in this kind of element.
 * !!!! TO DO: support model binding on Attributes too.
 *
 * @param <T> The type of the model binding to this HTML element.
 * @param <U> The type of HTML element returned by HtmlSelector methods.
 *
 * @author Miguel Gamboa
 *         created on 15-01-2016
 */
public abstract class HtmlSingleElement<T, U extends HtmlSingleElement>
        extends AbstractHtmlElementSelector<U> implements HtmlWriter<T> {
    /*=========================================================================
      -------------------------     FIELDS    ---------------------------------
      =========================================================================*/
    private PrintStream out;

    /*=========================================================================*/
    /*--------------------     HtmlWriter Methods   ----------------------------*/
    /*=========================================================================*/
    /**
     * Sets the current PrintStream.
     * @param out
     */
    public HtmlWriter<T> setPrintStream(PrintStream out){
        this.out = out;
        return this;
    }
    /**
     * @param depth The number of tabs indentation.
     * @param model An optional object model that could be bind to this element.
     */
    @Override
    public final void  write(int depth, T model) {
        tabs(out, depth);
        out.print(getElementValue());
    }
    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/
    private String getElementValue() {
        String tag = "<" + getElementName();
        for (Attribute attribute : getAttributes()) {
            tag += attribute.printAttribute();
        }
        return tag + "/>";
    }
}
