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
import htmlflow.visitor.HtmlVisitor;

/**
 * HtmlContinuation for a sequence of static blocks and value slots that we compiled to bytecode.
 * The generated code appends straight to a StringBuilder, so when the output is some other
 * Appendable we fall back to the linked chain.
 *
 * @author Bernardo Pereira
 */
final class HtmlContinuationSyncCompiled extends HtmlContinuation {

    private final Renderer renderer;

    private final HtmlContinuation template;

    /** The equivalent linked chain, which we only create when some render needs it. */
    private HtmlContinuation chain;

    HtmlContinuationSyncCompiled(
        Renderer renderer,
        HtmlContinuation template,
        HtmlVisitor visitor
    ) {
        super(-1, false, visitor, null);
        this.renderer = renderer;
        this.template = template;
    }

    /**
     * We do not synchronize here because concurrent calls just produce equal chains, and all the
     * fields of an HtmlContinuation are final.
     */
    private HtmlContinuation chain() {
        HtmlContinuation built = chain;
        if (built == null) chain = built = template.copy(visitor);
        return built;
    }

    /**
     * We override execute rather than emitHtml because a compiled chain has no indentation to
     * restore and no next node.
     */
    @Override
    public void execute(Object model) {
        Appendable out = visitor.out();
        if (out instanceof StringBuilder) {
            renderer.render(visitor, (StringBuilder) out, model);
        } else {
            chain().execute(model);
        }
    }

    @Override
    public HtmlContinuation copy(HtmlVisitor v) {
        return new HtmlContinuationSyncCompiled(renderer, template, v);
    }
}
