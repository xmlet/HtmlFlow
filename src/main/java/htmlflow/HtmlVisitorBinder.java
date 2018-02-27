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

import org.xmlet.htmlapi.AbstractElementVisitor;
import org.xmlet.htmlapi.Attribute;
import org.xmlet.htmlapi.Br;
import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.Hr;
import org.xmlet.htmlapi.Img;
import org.xmlet.htmlapi.Text;

import java.io.PrintStream;
import java.util.List;

/**
 * Type parameter T is the type of the model bound to this Visitor.
 *
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorBinder<T> extends AbstractElementVisitor<T> {

    private static final char BEGIN_TAG = '<';
    private static final char FINISH_TAG = '>';
    private static final char SPACE = ' ';
    private static final char QUOTATION = '"';
    private static final char EQUALS = '=';
    private static final char SLASH = '/';

    private static final Class<?>[] INLINES = {Img.class, Br.class, Hr.class};

    protected final PrintStream out;
    protected final T model;
    private int depth;

    public HtmlVisitorBinder(PrintStream out, T model) {
        this.out = out;
        this.model = model;
    }

    protected final void incTabs() { depth++;}
    protected final void decTabs() { depth--;}

    @Override
    public final <U extends Element> void visit(Element<U,?> elem) {
        out.println();
        tabs();
        printOpenTag(out, elem);
        incTabs();
        visitChildrem(elem);
        decTabs();
        if(isInline(elem)) return;
        out.println();
        tabs();
        out.print(BEGIN_TAG);      // <
        out.print(SLASH);          // </
        out.print(elem.getName()); // </name
        out.print(FINISH_TAG);     // </name>

    }

    protected <U extends Element> void visitChildrem(Element<U, ?> elem) {
        if(elem.isBound()) {
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
    public <R> void visit(Text<T, R, ?> text) {
        out.println();
        tabs();
        // The text element checks whether it has a model binder,
        // or a constant value.
        // It returns NULL whenever there is NO model binder.
        R val = text.getValue(model);
        out.print(val != null ? val : text.getValue());
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    final void tabs(){
        for (int i = 0; i < depth; i++)
            out.print('\t');
    }

    static final void printOpenTag(PrintStream out, Element<?, ?> elem) {
        out.print(BEGIN_TAG);
        out.print(elem.getName());
        for (Attribute attribute : elem.getAttributes()) {
            out.print(SPACE);
            out.print(attribute.getName());
            out.print(EQUALS);
            out.print(QUOTATION);
            out.print(attribute.getValue());
            out.print(QUOTATION);
        }
        if(isInline(elem)) out.print(SLASH);
        out.print(FINISH_TAG);
    }


    static final boolean isInline(Element elem) {
        Class<? extends Element> k = elem.getClass();
        for (int i = 0; i < INLINES.length; i++) {
            if(INLINES[i] == k)
                return true;
        }
        return false;
    }
}
