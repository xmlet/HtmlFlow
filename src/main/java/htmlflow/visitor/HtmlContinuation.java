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

/**
 * @param <U> the type of the template's model.
 */
public abstract class HtmlContinuation<U> {
    /**
     * A negative number means that should be ignored.
     */
    final int currentDepth;
    /**
     * In case of the middle of a beginning tag, it tells if it is closed, or not,
     * i.e. if it is {@code "<elem>"} or it is {@code "<elem"}.
     * On element visit the beginning tag is left open to include additional attributes.
     */
    final boolean isClosed;
    /**
     * HtmlVisitor that emits HTML and should be updated with correct indentation depth and isClosed.
     */
    final HtmlVisitor visitor;
    /**
     * Next HtmlContinuation
     */
    final HtmlContinuation<U> next;

    /**
     * @param currentDepth Indentation depth associated to this block.
     */
    public HtmlContinuation(int currentDepth, boolean isClosed, HtmlVisitor visitor, HtmlContinuation<U> next) {
        this.currentDepth = currentDepth;
        this.isClosed = isClosed;
        this.visitor = visitor;
        this.next = next;
    }

    public HtmlContinuation<U> getNext() {
        return next;
    }
    /**
     * Executes this continuation and calls the next one if exist.
     *
     * @param model
     */
    public final void execute(U model) {
        if (currentDepth >= 0) {
            this.visitor.isClosed = isClosed;
            this.visitor.depth = currentDepth;
        }
        emitHtml(model);
        if (next != null) {
            next.execute(model);
        }
    }
    /**
     * Hook method to emit HTML.
     *
     * @param model
     */
    abstract protected void emitHtml(U model);
    /**
     * Creates a copy of this HtmlContinuation with a new visitor
     *
     * @param visitor
     */
    abstract protected HtmlContinuation<U> copy(HtmlVisitor visitor);

}