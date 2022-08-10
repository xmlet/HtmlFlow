/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
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

package htmlflow.visitor;

import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Area;
import org.xmlet.htmlapifaster.Base;
import org.xmlet.htmlapifaster.Br;
import org.xmlet.htmlapifaster.Col;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.Embed;
import org.xmlet.htmlapifaster.Hr;
import org.xmlet.htmlapifaster.Img;
import org.xmlet.htmlapifaster.Input;
import org.xmlet.htmlapifaster.Link;
import org.xmlet.htmlapifaster.Meta;
import org.xmlet.htmlapifaster.Param;
import org.xmlet.htmlapifaster.Root;
import org.xmlet.htmlapifaster.Source;
import org.xmlet.htmlapifaster.Text;

import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This is the implementation of the ElementVisitor (from HtmlApiFaster
 * library) that emits HTML immediately with no optimizations.
 *
 * @author Miguel Gamboa
 *         created on 04-08-2022
 */
public abstract class HtmlVisitor extends ElementVisitor {
    /**
     * keep track of current indentation.
     */
    protected int depth;
    /**
     * If the begin tag is closed, or not, i.e. if it is {@code "<elem>"} or it is {@code "<elem"}.
     * On element visit the begin tag is left open to include additional attributes.
     */
    protected boolean isClosed = true;
    /**
     * It the HTML output should be indented or not.
     */
    public final boolean isIndented;
    /**
     * keep track of current indentation.
     */
    public final int getDepth() {
        return depth;
    }
    /**
     * Set current indentation.
     */
    public final void setDepth(int v) {
        depth = v;
    }

    HtmlVisitor(boolean isIndented) {
        this.isIndented = isIndented;
    }

    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr.
     * This method is invoked by visitParent specialization methods (at the end of this class)
     * for each void element such as area, base, etc.
     */
    protected abstract void visitParentOnVoidElements();

    /*=========================================================================*/
    /*------------            Abstract HOOK Methods         -------------------*/
    /*=========================================================================*/
    /**
     * Write {@code "<elementName"}.
     */
    protected abstract void beginTag(String elementName);

    /**
     * Writes {@code "</elementName>"}.
     */
    protected abstract void endTag(String elementName);

    /**
     * Writes {@code "attributeName=attributeValue"}
     */
    protected abstract void addAttribute(String attributeName, String attributeValue);

    /**
     * Writes {@code "<!--s-->"}
     */
    protected abstract void addComment(String s);


    /**
     * The number of characters written until this moment.
     */
    protected abstract int size();

    /**
     * Returns the accumulated output and clear it.
     */
    public abstract String finished();
    /**
     * Since HtmlVisitorCache is immutable this is the preferred way to create a copy of the
     * existing HtmlVisitorCache instance with a different isIndented state.
     *
     * @param isIndented If thenew visitor should indent HTML output or not.
     */
    public abstract HtmlVisitor clone(PrintStream out, boolean isIndented);

    /*=========================================================================*/
    /*------------            Root Element Methods         --------------------*/
    /*=========================================================================*/

    @Override
    public final <Z extends Element> void visitElementRoot(Root<Z> var1) { }

    @Override
    public final <Z extends Element> void visitParentRoot(Root<Z> var1) { }

    /*=========================================================================*/
    /*------------      Parent Methods for Void Elements   --------------------*/
    /*=========================================================================*/

    @Override
    public final <Z extends Element> void visitParentHr(Hr<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentEmbed(Embed<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentInput(Input<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentMeta(Meta<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentBr(Br<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentCol(Col<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentSource(Source<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentImg(Img<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentArea(Area<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentLink(Link<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentParam(Param<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentBase(Base<Z> element) {
        visitParentOnVoidElements ();
    }
}
