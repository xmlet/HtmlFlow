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

    private static final int tabsMax;
    private static String[] tabs;
    private static String[] closedTabs;

    static {
        tabsMax = 1000;
        tabs = HtmlUtils.createTabs(tabsMax);
        closedTabs = HtmlUtils.createClosedTabs(tabsMax);
    }

    protected final PrintStream out;
    private int depth;
    private boolean isClosed = true;

    HtmlVisitorPrintStream(PrintStream out) {
        this.out = out;
    }

    @Override
    public final void visitElement(String elementName) {
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.printOpenTag(out, elementName);
        isClosed = false;
    }

    @Override
    public void visitParent(String elementName) {
        if (isClosed){
            depth--;
        }

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
            depth++;
        }

        tabs();
        out.print(text);
    }

    @Override
    public <R> void visitComment(R comment) {
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.printComment(out, comment.toString());
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    private void tabs(){
        if (isClosed){
            out.print(tabs[depth]);
        } else {
            out.print(closedTabs[depth]);
            isClosed = true;
        }
    }

    /*=========================================================================*/
    /*--------------------      Parent Methods     ----------------------------*/
    /*=========================================================================*/

    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr.
     */
    private void visitParentSpecial(){
        if (!isClosed)
            HtmlTags.printOpenTagEnd(out);
        else
            depth--;
        isClosed = true;
    }

    @Override
    public final void visitParentHr() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentEmbed() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentInput() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentMeta() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentBr() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentCol() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentSource() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentImg() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentArea() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentLink() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentParam() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentBase() {
        visitParentSpecial();
    }
}
