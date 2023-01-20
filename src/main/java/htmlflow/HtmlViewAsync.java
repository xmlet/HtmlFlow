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

import htmlflow.visitor.HtmlViewVisitorAsync;
import org.xmlet.htmlapifaster.Html;

import java.util.concurrent.CompletableFuture;

/**
 * Dynamic views can be bound to a domain object within an asynchronous context with the usage of {@link org.reactivestreams.Publisher}.
 *
 * @author Pedro Fialho
 */
public class HtmlViewAsync extends HtmlPage {

    private final HtmlViewVisitorAsync visitor;

    HtmlViewAsync(HtmlViewVisitorAsync visitor) {
        this.visitor = visitor;
    }

    @Override
    public final Html<HtmlPage> html() {
        visitor.write(HEADER);
        return new Html<>(this);
    }

    @Override
    public HtmlPage setIndented(boolean isIndented) {
        return new HtmlViewAsync(visitor.clone(isIndented));
    }

    @Override
    public HtmlViewVisitorAsync getVisitor() {
        return visitor;
    }

    @Override
    public String getName() {
        return "HtmlViewAsync";
    }

    /**
     * This implementation is always thread-safe since we create a new visitor on each resolution (i.e. render or write).
     * Thus there is no shared visitors across concurrent resolutions.
     * @return
     */
    @Override
    public HtmlViewAsync threadSafe(){
        return this;
    }

    public final CompletableFuture<Void> writeAsync(Appendable out, Object model) {
        return visitor.clone(out).finishedAsync(model);
    }

    public final CompletableFuture<String> renderAsync(Object model) {
        StringBuilder str = new StringBuilder();
        return writeAsync(str, model).thenApply( nothing -> str.toString());
    }
}
