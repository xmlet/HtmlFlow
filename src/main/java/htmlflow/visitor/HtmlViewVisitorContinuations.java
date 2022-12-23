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

import htmlflow.HtmlView;
import htmlflow.continuations.HtmlContinuation;
import org.xmlet.htmlapifaster.Element;

import java.util.function.BiConsumer;

/**
 * HtmlViewVisitor which is also a head of HtmlContinuation objects chain, storing all
 * needed information to emit HTML corresponding to Static HTML blocks and dynamic HTML.
 *
 * @param <T>
 */
public abstract class HtmlViewVisitorContinuations extends HtmlViewVisitor {
    /**
     * The first node to be processed.
     */
    protected final HtmlContinuation first;
    /**
     * @param isIndented
     * @param first
     */
    HtmlViewVisitorContinuations(boolean isIndented, HtmlContinuation first) {
        super(isIndented);
        this.first = first.copy(this);
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
    public final void resolve(Object model, HtmlView...partials){
        first.execute(model);
    }
}
