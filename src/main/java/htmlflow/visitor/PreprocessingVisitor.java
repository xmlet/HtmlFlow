/*
 * MIT License
 *
 * Copyright (c) 2014-2022, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
 * and Pedro Fialho.
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

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationSyncCloseAndIndent;
import htmlflow.continuations.HtmlContinuationSyncDynamic;
import htmlflow.continuations.HtmlContinuationSyncStatic;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;

/**
 * This visitor is used to make a preprocessing resolution of an HtmlTemplate.
 * It will collect the resulting HTML from visiting static HTML elements into an auxiliary
 * StringBuilder that later is extracted to: String staticHtml = sb.substring(staticBlockIndex);
 * to create an HtmlContinuationSyncStatic object.
 * It also interleaves the creation of HtmlContinuationDynamic nodes that only store dynamicHtmlBlock
 * objects corresponding to a BiConsumer<E, U> (being E an element and U the model).
 * The U comes from external module HtmlApiFaster whose classes are not strongly typed with the Model.
 * Thus, only the dynamic() and visitDynamic() methods in HtmlApiFaster were made generic to carry
 * a type parameter U corresponding to the type of the Model.
 */
public class PreprocessingVisitor extends HtmlVisitor {
    private static final String NOT_SUPPORTED_ERROR =
        "This is a PreprocessingVisitor used to compile templates and not intended to support HTML views!";
    /**
     * The internal String builder beginning index of a static HTML block.
     */
    private int staticBlockIndex = 0;
    /**
     * The first node to be processed.
     */
    private HtmlContinuation first;
    /**
     * The last HtmlContinuation
     */
    private HtmlContinuation last;

    public PreprocessingVisitor(boolean isIndented) {
        super(new StringBuilder(), isIndented);
    }

    /**
     * The main StringBuilder.
     */
    public final StringBuilder sb() {
        return (StringBuilder) out;
    }

    public final HtmlContinuation getFirst() {
        return first;
    }

    /**
     * Here we are creating 2 HtmlContinuation objects: one for previous static HTML and a next one
     * corresponding to the consumer passed to dynamic().
     * We will first create the dynamic continuation that will be the next node of the static continuation.
     *
     * U is the type of the model passed to the dynamic HTML block that is the same as T in this visitor.
     * Yet, since it came from HtmlApiFaster that is not typed by the Model, then we have to use
     * another generic argument for the type of the model.
     *
     * @param element The parent element.
     * @param dynamicHtmlBlock The continuation that consumes the element and a model.
     * @param <E> Type of the parent Element.
     * @param <U> Type of the model passed to the dynamic HTML block that is the same as T in this visitor.
     */
    @Override
    public <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> dynamicHtmlBlock) {
        /**
         * Creates an HtmlContinuation for the dynamic block.
         */
        HtmlContinuation dynamicCont = new HtmlContinuationSyncDynamic<>(depth, isClosed, element, dynamicHtmlBlock, this, new HtmlContinuationSyncCloseAndIndent(this));
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the dynamicCont.
         */
        chainContinuationStatic(dynamicCont);
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        indentAndAdvanceStaticBlockIndex();
    }

    @Override
    public <M, E extends Element> void visitAwait(E element, AwaitConsumer<E, M> asyncAction) {
        throw new UnsupportedOperationException();
    }

    protected final void chainContinuationStatic(HtmlContinuation nextContinuation) {
        String staticHtml = sb().substring(staticBlockIndex);
        String staticHtmlTrimmed = staticHtml.trim();  // trim to remove the indentation from static block
        HtmlContinuation staticCont = new HtmlContinuationSyncStatic(staticHtmlTrimmed, this, nextContinuation);
        if(first == null) first = staticCont; // on first visit initializes the first pointer
        else setNext(last, staticCont);       // else append the staticCont to existing chain
        last = nextContinuation.next;         // advance last to point to the new HtmlContinuationCloseAndIndent
    }

    protected final void indentAndAdvanceStaticBlockIndex() {
        newlineAndIndent();
        staticBlockIndex = sb().length(); // increment the staticBlockIndex to the end of internal string buffer.
    }

    /**
     * Creates the last static HTML block.
     */
    @Override
    public void resolve(Object model) {
        String staticHtml = sb().substring(staticBlockIndex);
        HtmlContinuation staticCont = new HtmlContinuationSyncStatic(staticHtml.trim(), this, null);
        last = first == null
                ? first = staticCont         // assign both first and last
                : setNext(last, staticCont); // append new staticCont and return it to be the new last continuation.
    }

    @Override
    public final HtmlVisitor clone(boolean isIndented) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_ERROR);
    }

    @SuppressWarnings({"squid:S3011", "squid:S112"})
    static class HtmlContinuationSetter {
        private HtmlContinuationSetter() {
        }

        static final Field fieldNext;
        static {
            try {
                fieldNext = HtmlContinuation.class.getDeclaredField("next");
                fieldNext.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        static HtmlContinuation setNext(HtmlContinuation cont, HtmlContinuation next) {
            try {
                fieldNext.set(cont, next);
                return next;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
