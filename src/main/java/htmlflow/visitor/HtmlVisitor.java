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
    final boolean isIndented;
    /**
     * keep track of current indentation.
     */
    public int getDepth() {
        return depth;
    }
    /**
     * Set current indentation.
     */
    public void  setDepth(int v) {
        depth = v;
    }

    public boolean isIndented() {
        return isIndented;
    }

    HtmlVisitor(boolean isIndented) {
        this.isIndented = isIndented;
    }

    /**
     * This method appends the String {@code "<elementName"} and it leaves the element
     * open to include additional attributes.
     * Before that it may close the parent begin tag with {@code ">"} if it is
     * still opened (!isClosed).
     * The newlineAndIndent() is responsible for this job to check whether the parent element
     * is still opened or not.
     *
     * @param element
     */
    @Override
    public void visitElement(Element element) {
        newlineAndIndent();
        beginTag(element.getName()); // "<elementName"
        isClosed = false;
    }

    /**
     * Writes the end tag for elementName: {@code "</elementName>."}.
     * This visit occurs when the __() is invoked.
     */
    @Override
    public void visitParent(Element element) {
        depth--;
        newlineAndIndent();
        endTag(element.getName()); // </elementName>
    }

    @Override
    public void visitAttribute(String attributeName, String attributeValue) {
        addAttribute(attributeName, attributeValue);
    }

    @Override
    public <R> void visitText(Text<? extends Element, R> text) {
        newlineAndIndent();
        write(text.getValue());
    }


    @Override
    public <R> void visitComment(Text<? extends Element, R> text) {
        newlineAndIndent();
        addComment(text.getValue());
    }

    @Override
    public void visitOpenDynamic(){
        throw new IllegalStateException("Wrong use of dynamic() in a static view! Use HtmlView to produce a dynamic view.");
    }

    @Override
    public void visitCloseDynamic(){
        throw new IllegalStateException("Wrong use of dynamic() in a static view! Use HtmlView to produce a dynamic view.");
    }

    @Override
    public <E extends Element, T> void visitAsync(Supplier<E> element, BiConsumer<E, Observable<T>> asyncAction, Observable<T> obs) {
        throw new IllegalStateException("Wrong use of async() in a static view! Use HtmlView to produce an async view.");
    }

    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        throw new IllegalStateException("Wrong use of then() in a static view! Use HtmlView to produce an async view.");
    }

    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr.
     * This method is invoked by visitParent specialization methods (at the end of this class)
     * for each void element such as area, base, etc.
     */
    protected final void visitParentOnVoidElements(){
        if (!isClosed){
            write(Tags.FINISH_TAG);
        }
        isClosed = true;
    }

    /**
     * Adds a new line and indentation.
     * Checks whether the parent element is still opened or not (!isClosed).
     * If it is open then it closes the parent begin tag with ">" (!isClosed).
     */
    private void newlineAndIndent(){
        if (isClosed){
            if(isIndented) {
                write(Indentation.tabs(depth)); // \n\t\t\t\...
            }
        } else {
            depth++;
            if(isIndented)
                write(Indentation.closedTabs(depth)); // >\n\t\t\t\...
            else
                write(Tags.FINISH_TAG);
            isClosed = true;
        }
    }

    /**
     * Writes the {@code ">"} to output.
     */
    public final void closeBeginTag() {
        if(!isClosed) {
            write(Tags.FINISH_TAG);
            isClosed = true;
            depth++;
        }
    }
    /**
     * This visitor may be writing to output or not, depending on the visitor's
     * strategy and the kind of HTML block that it is being visited.
     */
    public abstract boolean isWriting();

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
     * Writes the string text directly to the output.
     */
    public abstract void write(String text);

    /**
     * Writes the char c directly to the output.
     */
    protected abstract void write(char c);

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
    /**
     * Creates a new similar instance with all static bocks cleared.
     */
    public abstract HtmlVisitor newbie();

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
