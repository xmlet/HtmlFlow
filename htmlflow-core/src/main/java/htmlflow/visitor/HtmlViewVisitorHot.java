/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
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

import java.util.function.BiConsumer;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

/**
 * Hot reload visitor that processes templates without preprocessing optimization. This visitor
 * recalculates the entire HTML on every rendering, making it suitable for development scenarios
 * where template changes need immediate reflection.
 *
 * <p><strong>Limitations:</strong>
 *
 * <ul>
 *   <li>Does not support async operations (visitAwait)
 *   <li>Does not support suspending operations (visitSuspending)
 *   <li>Performance is significantly slower than optimized visitor
 * </ul>
 *
 * @see HtmlViewVisitor for optimized visitor with preprocessing
 */
public class HtmlViewVisitorHot extends HtmlVisitor {

    private Object model;

    public HtmlViewVisitorHot(Appendable out, boolean isIndented) {
        super(out, isIndented);
    }

    @Override
    public void resolve(Object model) {
        this.model = model;
    }

    @Override
    public HtmlVisitor clone(boolean isIndented) {
        return new HtmlViewVisitorHot(out, isIndented);
    }

    @Override
    public <E extends Element, U> void visitDynamic(
        E e,
        BiConsumer<E, U> biConsumer
    ) {
        biConsumer.accept(e, (U) model);
    }

    @Override
    public <M, E extends Element> void visitAwait(
        E e,
        AwaitConsumer<E, M> awaitConsumer
    ) {
        throw new UnsupportedOperationException(
            "Hot reload visitor does not support async operations. Use HtmlViewVisitorAsync" +
            " instead."
        );
    }
}
