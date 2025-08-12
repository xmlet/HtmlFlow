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

import htmlflow.visitor.HtmlVisitor;

import java.util.function.Supplier;

/**
 * Dynamic views can be bound to a Model object and unoptimized.
 * It does not store static HTML blocks and recalculate HTML on every rendering.
 *
 * @param <M> Type of the model rendered with this view.
 *
 * @author Miguel Gamboa
 */
public class HtmlViewHot<M> extends HtmlView<M> {

    /**
     * Auxiliary constructor used by clone().
     *
     * @param visitorSupplier Supplier that provides a new instance of HtmlVisitor.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param threadSafe If true, the view is thread-safe and uses a ThreadLocal visitor.
     */
    HtmlViewHot(Supplier<HtmlVisitor> visitorSupplier, HtmlTemplate template, boolean threadSafe) {
        super(visitorSupplier, template, threadSafe);
    }

    @Override
    public String getName() {
        return "HtmlViewHot";
    }

    public String render() {
        return render(null);
    }

    public String render(M model) {
        StringBuilder out = (StringBuilder) getVisitor().out();
        out.setLength(0);
        getVisitor().resolve(model);
        template.resolve(this);
        return out.toString().trim();
    }

    public void write(M model) {
        getVisitor().resolve(model);
        this.template.resolve(this);
    }

    public void write() {
        write(null);
    }

    protected HtmlViewHot<M> clone(
            Supplier<HtmlVisitor> visitorSupplier,
            boolean threadSafe)
    {
        return new HtmlViewHot<>(visitorSupplier, template, threadSafe);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HtmlViewHot<M> setIndented(boolean isIndented) {
        return (HtmlViewHot<M>) HtmlFlow.view(template, isIndented, threadSafe, false);
    }
}
