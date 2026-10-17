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

import htmlflow.exceptions.HtmlFlowAppendException;
import htmlflow.visitor.HtmlVisitor;
import java.io.IOException;

/**
 * Base class of the classes that {@link htmlflow.continuations.codegen.ChainCompiler} generates
 * for a chain of static blocks and value slots. The generated class implements {@link #write},
 * and like a continuation it writes to the output of its visitor.
 */
public abstract class HtmlResolverCompiled implements HtmlResolver, Cloneable {

    private HtmlVisitor visitor;

    protected HtmlResolverCompiled() {}

    @Override
    public final void resolve(Object model) {
        try {
            write(visitor.out(), model);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /** Shares the accessors with this resolver and writes through the given visitor. */
    @Override
    public final HtmlResolver copy(HtmlVisitor visitor) {
        try {
            HtmlResolverCompiled copy = (HtmlResolverCompiled) clone();
            copy.visitor = visitor;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e); // unreachable, this class is Cloneable
        }
    }

    protected abstract void write(Appendable out, Object model)
        throws IOException;
}
