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

import htmlflow.exceptions.HtmlFlowAppendException;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * This is the implementation of the ElementVisitor (from HtmlApiFaster
 * library) that emits HTML immediately with no optimizations.
 *
 * @author Miguel Gamboa
 *         created on 04-08-2022
 */
public class HtmlDocVisitor extends HtmlVisitor implements TagsToAppendable {

    private final Appendable out;

    public HtmlDocVisitor(Appendable out, boolean isIndented) {
        this(out, isIndented, 0);
    }

    public HtmlDocVisitor(Appendable out,boolean isIndented, int depth) {
        super(isIndented);
        this.out = out;
        this.depth = depth;
    }

    @Override
    public final <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> dynamicHtmlBlock) {
        throw new IllegalStateException("Wrong use of dynamic() in a static view! Use HtmlView to produce a dynamic view.");
    }

    @Override
    public final <M, E extends Element> void visitAwait(E element, AwaitConsumer<E,M> asyncAction) {
        throw new IllegalStateException("Wrong use of async() in a static view! Use HtmlView to produce an async view.");
    }

    @Override
    public final void write(String text) {
        try {
            out.append(text);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    protected final void write(char c) {
        try {
            out.append(c);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    public final HtmlDocVisitor clone(boolean isIndented) {
        return new HtmlDocVisitor(this.out, isIndented);
    }

    @Override
    public Appendable out() {
        return out;
    }

    /**
     * Returns the accumulated output and clear it.
     */
    public String finish() {
        if (out instanceof StringBuilder) {
            return ((StringBuilder) out).toString();
        }
        throw new UnsupportedOperationException("Do not call finish() with non String Builder Appendable because" +
                "HTML fragments have been already emitted on each element call.");
    }
}
