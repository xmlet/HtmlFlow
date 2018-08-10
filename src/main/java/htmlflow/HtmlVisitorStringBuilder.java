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

import org.xmlet.htmlapifaster.Attribute;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.Text;

/**
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorStringBuilder extends ElementVisitor {

    static final char NEWLINE = '\n';

    protected final StringBuilder sb;
    private int depth;
    private boolean isClosed = true;

    public HtmlVisitorStringBuilder(StringBuilder sb) {
        this.sb = sb;
    }

    protected final void incTabs() { depth++;}
    protected final void decTabs() { depth--;}

    @Override
    public final void visit(Element elem) {
        if (!isClosed){
            HtmlTags.appendOpenTagEnd(sb);
            incTabs();
        }
        sb.append(NEWLINE);
        tabs();
        HtmlTags.appendOpenTag(sb, elem);
        isClosed = false;
    }

    @Override
    public void visitParent(Element elem) {
        if (!isClosed)
            HtmlTags.appendOpenTagEnd(sb);
        else
            decTabs();
        isClosed = true;

        if(HtmlTags.isVoidElement(elem)) return;
        sb.append(NEWLINE);
        tabs();
        HtmlTags.appendCloseTag(sb, elem);
    }

    @Override
    public void visit(Attribute attribute) {
        HtmlTags.appendAttribute(sb, attribute);
    }

    /**
     * An optimized version of Text without binder.
     */
    @Override
    public void visit(Text text) {
        if (!isClosed){
            HtmlTags.appendOpenTagEnd(sb);
            incTabs();
            isClosed = true;
        }
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
