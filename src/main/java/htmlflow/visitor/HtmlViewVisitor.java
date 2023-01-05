/*
 * MIT License
 *
 * Copyright (c) 2014-22, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
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
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.function.BiConsumer;


/**
 * This is the base implementation of the ElementVisitor (from HtmlApiFaster library).
 *
 * HtmlViewVisitor is also a head of HtmlContinuation objects chain, storing all
 * needed information to emit HTML corresponding to Static HTML blocks and dynamic HTML.
 *
 * @author Miguel Gamboa, Luís Duare, Pedro Fialho
 *         created on 17-01-2018
 */
public class HtmlViewVisitor extends HtmlVisitor {
    /**
     * The first node to be processed.
     */
    public final HtmlContinuation first;

    public HtmlViewVisitor(Appendable out, boolean isIndented, HtmlContinuation first) {
        super(out, isIndented);
        this.first = first.copy(this);
    }

    /**
     * Processing output through invocation of HtmlContinuation objects chain.
     */
    @Override
    public final void resolve(Object model) {
        first.execute(model);
    }

    @Override
    public HtmlVisitor clone(boolean isIndented) {
        return new HtmlViewVisitor(out, isIndented, first);
    }

    /**
     * We only allow a single call to visitDynamic when we are preprocessing the template function
     * and building an invocation chain of HtmlContinuation objects (see PreprocessingVisitor).
     *
     * @param element The parent Element.
     * @param dynamicHtmlBlock The continuation that consumes the element and a model.
     */
    @Override
    public final <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> dynamicHtmlBlock) {
        /**
         * After first resolution we will only invoke the dynamicHtmlBlock consumer and no more visits
         * to dynamic can happen.
         * Otherwise, maybe we are invoking a dynamic nested in other dynamic, which is not allowed!
         */
        throw new IllegalStateException("You are already in a dynamic block! Do not use dynamic() chained inside another dynamic!");
    }

    @Override
    public <M, E extends Element> void visitAwait(E element, AwaitConsumer<E,M> asyncAction) {
        throw new IllegalStateException("Wrong use of await() in a HtmlView! Use HtmlFlow.viewAsync() to produce an async view.");
    }
}
