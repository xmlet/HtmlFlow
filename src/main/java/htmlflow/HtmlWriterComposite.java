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
import java.util.LinkedList;
import java.util.List;

/**
 * It is an {@link HtmlWriter} object containing other {@link HtmlWriter} objects
 * following a composite approach.
 *
 * @param <T> The type of the model binding to this HTML element.
 * @param <U> The type of HTML element returned by HtmlSelector methods.
 *
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public abstract class HtmlWriterComposite<T, U extends HtmlWriterComposite>
        extends AbstractHtmlElementSelector<U> implements HtmlWriter<T> {

    /*=========================================================================*/
    /*-------------------------     FIELDS    ---------------------------------*/
    /*=========================================================================*/

    /**
     * @uml.associationEnd  aggregation="shared" inverse="htmlflow.HtmlWriter" multiplicity="(0 -1)"
     */
    private final List<HtmlWriter<?>> children;
    private PrintStream out;

    /*=========================================================================*/
    /*-------------------------  CONSTRUCTOR  ---------------------------------*/
    /*=========================================================================*/
    public HtmlWriterComposite() {
        children = new LinkedList<>();
    }

    /*=========================================================================*/
    /*--------------------- HtmlWriter interface -----------------------------*/
    /*=========================================================================*/
    public HtmlWriter<T> setPrintStream(PrintStream out){
        this.out = out;
        for (HtmlWriter elem : children) {
            elem.setPrintStream(out);
        }
        return this;
    }

    @Override
    public final void  write(int depth, T model) {
        doWriteBefore(out, depth);
        boolean doTab;
        if(children!= null && !children.isEmpty() && children.get(0) != null && children.get(0) instanceof TextNode){
            out.print("");
            doTab = false;
        }else{
            out.println();
            doTab = true;
        }
        for (HtmlWriter elem : children) {
            elem.write(depth+1, model);
        }
        doWriteAfter(out, depth, doTab);
        out.flush();
    }

    /*=========================================================================*/
    /*----------------------- Instance Methods --------------------------------*/
    /*=========================================================================*/

    public<S extends HtmlWriter<?>> S addChild(S child){
        children.add(child);
        return child;
    }

    public void doWriteBefore(PrintStream out, int depth) {
        tabs(out, depth);
        out.print(getElementValue());
    }


    public void doWriteAfter(PrintStream out, int depth, boolean doTab) {
        // RMK : do not insert tabs after a text node
        if(doTab){
            tabs(out, depth);
        }
        out.println("</"+ getElementName()+">");
    }

    protected String getElementValue() {
        String tag = "<" + getElementName();
        for (Attribute attribute : getAttributes()) {
            tag += attribute.printAttribute();
        }
        return  tag+">";
    }
}