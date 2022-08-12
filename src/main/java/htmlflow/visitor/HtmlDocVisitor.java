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
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Text;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This is the implementation of the ElementVisitor (from HtmlApiFaster
 * library) that emits HTML immediately with no optimizations.
 *
 * @author Miguel Gamboa
 *         created on 04-08-2022
 */
public abstract class HtmlDocVisitor extends HtmlVisitor {

    HtmlDocVisitor(boolean isIndented) {
        super(isIndented);
    }

    /**
     * Adds a new line and indentation.
     * Checks whether the parent element is still opened or not (!isClosed).
     * If it is open then it closes the parent begin tag with ">" (!isClosed).
     * REMARK intentionally duplicating this method in other HtmlVisitor implementations,
     * to improve performance.
     */
    protected final void newlineAndIndent(){
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
    public final void visitElement(Element element) {
        newlineAndIndent();
        beginTag(element.getName()); // "<elementName"
        isClosed = false;
    }

    /**
     * Writes the end tag for elementName: {@code "</elementName>."}.
     * This visit occurs when the __() is invoked.
     */
    @Override
    public final void visitParent(Element element) {
        depth--;
        newlineAndIndent();
        endTag(element.getName()); // </elementName>
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

    @Override
    public final void visitAttribute(String attributeName, String attributeValue) {
        addAttribute(attributeName, attributeValue);
    }

    @Override
    public final <R> void visitText(Text<? extends Element, R> text) {
        newlineAndIndent();
        write(text.getValue());
    }


    @Override
    public final <R> void visitComment(Text<? extends Element, R> text) {
        newlineAndIndent();
        addComment(text.getValue());
    }

    @Override
    public final void visitOpenDynamic(){
        throw new IllegalStateException("Wrong use of dynamic() in a static view! Use HtmlView to produce a dynamic view.");
    }

    @Override
    public final void visitCloseDynamic(){
        throw new IllegalStateException("Wrong use of dynamic() in a static view! Use HtmlView to produce a dynamic view.");
    }

    @Override
    public final <E extends Element, T> void visitAsync(Supplier<E> element, BiConsumer<E, Observable<T>> asyncAction, Observable<T> obs) {
        throw new IllegalStateException("Wrong use of async() in a static view! Use HtmlView to produce an async view.");
    }

    @Override
    public final <E extends Element> void visitThen(Supplier<E> elem) {
        throw new IllegalStateException("Wrong use of then() in a static view! Use HtmlView to produce an async view.");
    }
    /*=========================================================================*/
    /*------------            Abstract HOOK Methods         -------------------*/
    /*=========================================================================*/
    /**
     * Writes the string text directly to the output.
     */
    public abstract void write(String text);

    /**
     * Writes the char c directly to the output.
     */
    protected abstract void write(char c);
}
