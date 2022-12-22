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

package htmlflow.continuations;

import htmlflow.visitor.HtmlVisitor;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;

/**
 * HtmlContinuation for a dynamic block (i.e. BiConsumer) depending of an object model.
 *
 * @param <E> the type of the parent HTML element received by the dynamic HTML block.
 * @param <T> the type of the template's model.
 */
public class HtmlContinuationSyncDynamic<E extends Element, T> extends HtmlContinuationSync {

    /**
     * The continuation that consumes the element and a model.
     */
    final BiConsumer<E, T> consumer;
    /**
     * The element passed to the continuation consumer.
     */
    final E element;

    /**
     * @param currentDepth Indentation depth associated to this block.
     * @param consumer     The continuation that consumes the element and a model.
     */
    public HtmlContinuationSyncDynamic(
        int currentDepth,
        boolean isClosed,
        E element, BiConsumer<E, T> consumer,
        HtmlVisitor visitor,
        HtmlContinuation next
    ) {
        super(currentDepth, isClosed, visitor, next);
        this.element = element;
        this.consumer = consumer;
    }
    @Override
    protected final void emitHtml(Object model) {
        consumer.accept(element, (T) model);
    }

    @Override
    public HtmlContinuation copy(HtmlVisitor v) {
        return new HtmlContinuationSyncDynamic<>(
            currentDepth,
            isClosed,
            copyElement(v),
            consumer,
            v,
            next != null ? next.copy(v) : null); // call copy recursively
    }

    @SuppressWarnings("squid:S3011")
    public E copyElement(HtmlVisitor v) {
        try {
            Constructor<E> ctor = ((Class<E>) element
                .getClass())
                .getDeclaredConstructor(Element.class, ElementVisitor.class, boolean.class);
            ctor.setAccessible(true);
            return ctor.newInstance(element.getParent(), v, false); // false to not dispatch visit now
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
