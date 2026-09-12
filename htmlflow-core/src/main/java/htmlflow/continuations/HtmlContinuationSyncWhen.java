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

package htmlflow.continuations;

import htmlflow.visitor.HtmlVisitor;
import java.util.function.Predicate;

/**
 * HtmlContinuation for a conditional, which emits one of two preencoded HTML blocks. The orElse
 * block is null when the template does not provide it.
 *
 * @author Bernardo Pereira
 */
public class HtmlContinuationSyncWhen<M, T> extends HtmlContinuationSync {

    private final Predicate<M> condition;

    private final HtmlContinuation body;

    private final HtmlContinuation orElse;

    public HtmlContinuationSyncWhen(
        Predicate<M> condition,
        HtmlContinuation body,
        HtmlContinuation orElse,
        HtmlVisitor visitor,
        HtmlContinuation next
    ) {
        super(-1, false, visitor, next);
        this.condition = condition;
        this.body = body;
        this.orElse = orElse;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final void emitHtml(Object model) {
        if (condition.test((M) model)) {
            body.execute(model);
        } else if (orElse != null) {
            orElse.execute(model);
        }
    }

    @Override
    public HtmlContinuation copy(HtmlVisitor v) {
        return new HtmlContinuationSyncWhen<>(
            condition,
            body.copy(v),
            orElse == null ? null : orElse.copy(v),
            v,
            next != null ? next.copy(v) : null
        );
    }
}
