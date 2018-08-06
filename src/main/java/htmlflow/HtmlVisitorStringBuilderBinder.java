/*
 * MIT License
 *
 * Copyright (c) 2014-16, mcarvalho (gamboa.pt)
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

import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.ElementVisitor;
import org.xmlet.htmlapi.Text;
import org.xmlet.htmlapi.TextFunction;

import java.util.List;

/**
 * Type parameter T is the type of the model bound to this Visitor.
 *
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorStringBuilderBinder<T> extends ElementVisitor<T> {

    static final char NEWLINE = '\n';

    protected final StringBuilder sb;
    protected final T model;
    private int depth;

    public HtmlVisitorStringBuilderBinder(StringBuilder sb, T model) {
        this.sb = sb;
        this.model = model;
    }

    protected final void incTabs() { depth++;}
    protected final void decTabs() { depth--;}

    @Override
    public final <U extends Element> void sharedVisit(Element<U,?> elem) {
        sb.append(NEWLINE);
        tabs();
        HtmlTags.appendOpenTag(sb, elem);
        incTabs();
        visitChildrem(elem);
        decTabs();
        if(HtmlTags.isVoidElement(elem)) return;
        sb.append(NEWLINE);
        tabs();
        HtmlTags.appendCloseTag(sb, elem);
    }

    protected <U extends Element> void visitChildrem(Element<U, ?> elem) {
        if(elem.isBound()) {
            /**
             * Some binding function append new elements and change the original
             * parent element. Thus we clone it to preserve its original structure
             * and guarantee the same behavior on next traversals.
             */
            List<Element> children = elem.cloneElem().bindTo(model).getChildren();
            children.forEach(item -> item.accept(this));
        } else {
            elem.getChildren().forEach(item -> item.accept(this));
        }

    }

    /**
     * Type parameter R is the type of property in model T.
     */
    @Override
    public <R> void visit(TextFunction<T, R, ?> text) {
        sb.append(NEWLINE);
        tabs();
        // The text element checks whether it has a model binder.
        // It returns NULL whenever there is NO model binder.
        R val = text.getValue(model);
        sb.append(val);
    }

    /**
     * An optimized version of Text without binder.
     */
    @Override
    public void visit(Text text) {
        sb.append(NEWLINE);
        tabs();
        sb.append(text.getValue());
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    final void tabs(){
        for (int i = 0; i < depth; i++)
            sb.append('\t');
    }
}
