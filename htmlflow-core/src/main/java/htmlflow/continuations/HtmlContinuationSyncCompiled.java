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

import htmlflow.continuations.codegen.ChainCompiler.Renderer;
import htmlflow.exceptions.HtmlFlowAppendException;
import htmlflow.visitor.HtmlVisitor;
import java.io.IOException;

/**
 * HtmlContinuation for a chain of static blocks and value slots compiled to bytecode. A
 * StringBuilder gets the render method, which appends numbers without boxing, and any other
 * Appendable gets the write method.
 *
 * @author Bernardo Pereira
 */
final class HtmlContinuationSyncCompiled extends HtmlContinuation {

    private final Renderer renderer;

    HtmlContinuationSyncCompiled(Renderer renderer, HtmlVisitor visitor) {
        super(-1, false, visitor, null);
        this.renderer = renderer;
    }

    @Override
    public void execute(Object model) {
        Appendable out = visitor.out();
        if (out instanceof StringBuilder) {
            renderer.render((StringBuilder) out, model);
            return;
        }
        try {
            renderer.write(out, model);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    @Override
    public HtmlContinuation copy(HtmlVisitor v) {
        return new HtmlContinuationSyncCompiled(renderer, v);
    }
}
