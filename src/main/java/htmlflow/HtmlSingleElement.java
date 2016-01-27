/*
 * Copyright (c) 2016, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
