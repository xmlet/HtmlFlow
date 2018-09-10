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

import org.xmlet.htmlapifaster.ElementVisitor;

/**
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorStringBuilder extends ElementVisitor {

    private static final int tabsMax;
    private static String[] tabs;
    private static String[] closedTabs;

    static {
        tabsMax = 1000;
        tabs = HtmlUtils.createTabs(tabsMax);
        closedTabs = HtmlUtils.createClosedTabs(tabsMax);
    }

    private final StringBuilder sb;
    private int depth;
    private boolean isClosed = true;

    HtmlVisitorStringBuilder(StringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public final void visitElement(String elementName) {
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.appendOpenTag(sb, elementName);
        isClosed = false;
    }

    @Override
    public final void visitParent(String elementName) {
        if (isClosed){
            depth--;
        }

        tabs();
        HtmlTags.appendCloseTag(sb, elementName);
    }

    @Override
    public final void visitAttribute(String attributeName, String attributeValue) {
        HtmlTags.appendAttribute(sb, attributeName, attributeValue);
    }

    /**
     * An optimized version of Text without binder.
     */
    @Override
    public final <R> void visitText(R text) {
        if (!isClosed){
            depth++;
        }

        tabs();
        sb.append(text);
    }

    @Override
    public final <R> void visitComment(R comment) {
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.appendComment(sb, comment.toString());
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    private void tabs(){
        if (isClosed){
            sb.append(tabs[depth]);
        } else {
            sb.append(closedTabs[depth]);
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
            HtmlTags.appendOpenTagEnd(sb);
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
