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

import org.xmlet.htmlapifaster.ElementVisitor;

import java.io.PrintStream;

/**
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorPrintStream extends ElementVisitor {

    protected final PrintStream out;
    private int depth;
    private boolean isClosed = true;

    public HtmlVisitorPrintStream(PrintStream out) {
        this.out = out;
    }

    protected final void incTabs() { depth++;}
    protected final void decTabs() { depth--;}

    @Override
    public final void visitElement(String elementName) {
        if (!isClosed){
            HtmlTags.printOpenTagEnd(out);
            incTabs();
        }
        out.println();
        tabs();
        HtmlTags.printOpenTag(out, elementName);
        isClosed = false;
    }

    @Override
    public void visitParent(String elementName) {
        if (!isClosed)
            HtmlTags.printOpenTagEnd(out);
        else
            decTabs();
        isClosed = true;

        if(HtmlTags.isVoidElement(elementName)) return;
        out.println();
        tabs();
        HtmlTags.printCloseTag(out, elementName);
    }

    @Override
    public void visitAttribute(String attributeName, String attributeValue) {
        HtmlTags.printAttribute(out, attributeName, attributeValue);
    }

    /**
     * An optimized version of Text without binder.
     */
    @Override
    public <R> void visitText(R text) {
        if (!isClosed){
            HtmlTags.printOpenTagEnd(out);
            incTabs();
            isClosed = true;
        }
        out.println();
        tabs();
        out.print(text);
    }

    @Override
    public <R> void visitComment(R comment) {
        if (!isClosed){
            HtmlTags.printOpenTagEnd(out);
            incTabs();
            isClosed = true;
        }
        out.println();
        tabs();
        HtmlTags.printOpenComment(out);
        out.print(comment);
        HtmlTags.printEndComment(out);
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    final void tabs(){
        for (int i = 0; i < depth; i++)
            out.print('\t');
    }
}
