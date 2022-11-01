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

package htmlflow;

import htmlflow.visitor.HtmlViewVisitor;
import htmlflow.visitor.HtmlVisitor;
import htmlflow.visitor.HtmlVisitorAsync;
import htmlflow.visitor.HtmlVisitorAsyncWithPreProcessor;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Dynamic views can be bound to a domain object within an asynchronous context with the usage of {@link org.reactivestreams.Publisher}.
 *
 * @param <T> The type of async domain object bound to this View.
 *
 * @author Pedro Fialho
 */
public class HtmlViewAsync<T> extends HtmlView<T> {
    
    private static final String WRONG_USE_OF_RENDER_WITHOUT_MODEL =
            "Wrong use of HtmlViewAsync! You should provide a " +
                    "model parameter or use a static view instead!";
    
    private static final String WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR =
            "Wrong use of HtmlViewAsync writeAsync! You should use the viewAsync creation instead!";
    
    private static final String WRONG_USE_OF_RENDER_WITH_ASYNC_VIEW =
            "Wrong use of HtmlViewAsync! You should use the writeAsync method instead";
    
    /**
     * Auxiliary constructor used by clone().
     *
     * @param out
     * @param visitorSupplier
     * @param threadSafe
     */
    HtmlViewAsync(PrintStream out, Supplier<HtmlViewVisitor<T>> visitorSupplier,
                  boolean threadSafe) {
        super(out, visitorSupplier, threadSafe);
    }
    
    @Override
    public String getName() {
        return "HtmlViewAsync";
    }
    
    @Override
    public final String render() {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }
    
    @Override
    public final String render(T model) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITH_ASYNC_VIEW);
    }
    
    @Override
    public final void write() {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }
    
    @Override
    public final void write(T model) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }
    
    public final CompletableFuture<Void> writeAsync(T model) {
        final HtmlVisitor localVisitor = this.getVisitor();
        
        if (!(localVisitor instanceof HtmlVisitorAsyncWithPreProcessor)) {
            throw new UnsupportedOperationException(WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR);
        }
        
        HtmlVisitorAsyncWithPreProcessor visitorAsync = (HtmlVisitorAsyncWithPreProcessor) localVisitor;
        /**
         * 1st run binder to create AsyncNode linked instances.
         * 2nd finishedAsync() to register a CompletableFuture on completion of last AsyncNode
         * 3rd Call execute() on first AsyncNode that will propagate execute() to the next node and henceforward.
         */
        return (CompletableFuture<Void>) visitorAsync.finishedAsync(model);
    }
    
    public final CompletableFuture<Void> writeAsync(T model, HtmlView... partials) {
        final HtmlVisitor localVisitor = this.getVisitor();
        
        if (!(localVisitor instanceof HtmlVisitorAsync)) {
            throw new UnsupportedOperationException(WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR);
        }
        
        HtmlVisitorAsync visitorAsync = (HtmlVisitorAsync) localVisitor;
        return visitorAsync.finishedAsync();
    }
}
