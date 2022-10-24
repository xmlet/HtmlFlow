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

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;

/**
 * @param <U> the type of the template's model.
 */
public class HtmlContinuationDynamic<E extends Element, U> extends HtmlContinuation<U> {

    /**
     * The continuation that consumes the element and a model.
     */
    final BiConsumer<E, U> consumer;
    /**
     * The element passed to the continuation consumer.
     */
    final E element;

    /**
     * @param currentDepth Indentation depth associated to this block.
     * @param consumer     The continuation that consumes the element and a model.
     */
    HtmlContinuationDynamic(
        int currentDepth,
        boolean isClosed,
        E element, BiConsumer<E, U> consumer,
        HtmlVisitor visitor,
        HtmlContinuation next
    ) {
        super(currentDepth, isClosed, visitor, next);
        this.element = element;
        this.consumer = consumer;
    }

    @Override
    protected void emitHtml(U model) {
        consumer.accept(element, model);
    }

    @Override
    protected HtmlContinuation<U> copy(HtmlVisitor v) {
        return new HtmlContinuationDynamic<>(
            currentDepth,
            isClosed,
            copyElement(v),
            consumer,
            v,
            next != null ? next.copy(v) : null); // call copy recursively
    }

    public E copyElement(HtmlVisitor v) {
        try {
            Constructor<E> ctor = ((Class<E>) element
                .getClass())
                .getDeclaredConstructor(Element.class, ElementVisitor.class, boolean.class);
            ctor.setAccessible(true);
            return ctor.newInstance(element.getParent(), v, false); // false to not dispatch visit now
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
