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

package htmlflow;

import java.io.*;
import java.util.function.BiConsumer;

/**
 * Dynamic views can be bound to a domain object.
 *
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa, Luís Duare
 */
public class DynamicHtml<T> extends HtmlView<T> {

    private static final String WRONG_USE_OF_RENDER_WITHOUT_MODEL =
             "Wrong use of DynamicView! You should provide a " +
             "model parameter or use a static view instead!";

    private BiConsumer<DynamicHtml<T>, T> template;

    public static <U> DynamicHtml<U> view(PrintStream out, BiConsumer<DynamicHtml<U>, U> template){
        return new DynamicHtml<>(out, template);
    }

    public static <U> DynamicHtml<U> view(BiConsumer<DynamicHtml<U>, U> template){
        return new DynamicHtml<>(template);
    }

    public DynamicHtml(PrintStream out, BiConsumer<DynamicHtml<T>, T> template) {
        super(out);
        this.template = template;
    }

    public DynamicHtml(BiConsumer<DynamicHtml<T>, T> template) {
        this.template = template;
    }

    @Override
    public String render() {
        if(visitor.get() instanceof HtmlVisitorPrintStream)
            throw new IllegalStateException(WRONG_USE_OF_RENDER_WITH_PRINTSTREAM);
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }

    @Override
    public String render(T model) {
        if(visitor.get() instanceof HtmlVisitorPrintStream)
            throw new IllegalStateException(WRONG_USE_OF_RENDER_WITH_PRINTSTREAM);
        template.accept(this, model);
        return visitor.get().finished();
    }

    @Override
    public void write() {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }

    @Override
    public void write(T model) {
        template.accept(this, model);
        visitor.get().finished();
    }

    /**
     * Adds a partial view to this view.
     *
     * @param partial inner view.
     * @param model the domain object bound to the partial view.
     * @param <U> the type of the domain model of the partial view.
     */
    public <U> void addPartial(HtmlView<U> partial, U model) {
        HtmlVisitorCache visitor = this.visitor.get();

        visitor.closeBeginTag();
        partial.visitor.get().depth = visitor.depth;
        visitor.write(partial.render(model));
    }

    /**
     * Adds a partial view to this view.
     *
     * @param partial inner view.
     * @param <U> the type of the domain model of the partial view.
     */
    public <U> void addPartial(HtmlView<U> partial) {
        HtmlVisitorCache visitor = this.visitor.get();

        visitor.closeBeginTag();
        partial.visitor.get().depth = visitor.depth;
        visitor.write(partial.render());
    }
}
