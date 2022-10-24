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

import htmlflow.HtmlView;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.OnPublisherCompletion;

import java.util.function.BiConsumer;
import java.util.function.Supplier;


/**
 * This is the base implementation of the ElementVisitor (from HtmlApiFaster
 * library) with an associated type of model T.
 *
 * @param <T> The type of domain object (i.e. the Model)
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 17-01-2018
 */
public abstract class HtmlViewVisitor<T> extends HtmlVisitor {

    HtmlViewVisitor(boolean isIndented) {
        super(isIndented);
    }

    @Override
    public <E extends Element, T> OnPublisherCompletion visitAsync(Supplier<E> element, BiConsumer<E, Publisher<T>> asyncAction, Publisher<T> obs) {
        throw new IllegalStateException("Wrong use of async() in a HtmlView! Use HtmlFlow.viewAsync() to produce an async view.");
    }

    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        throw new IllegalStateException("Wrong use of then() in a HtmlView! Use HtmlFlow.viewAsync() to produce an async view.");
    }

    /*=========================================================================*/
    /*------------            Abstract HOOK Methods         -------------------*/
    /*=========================================================================*/
    /**
     * Returns the accumulated output and clear it.
     */
    public abstract String finish(T model, HtmlView...partials);
}
