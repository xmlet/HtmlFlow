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
import org.xmlet.htmlapifaster.Html;

/**
 * Dynamic views can be bound to a Model object.
 *
 * @param <M> Type of the model rendered with this view.
 * @author Miguel Gamboa, Luís Duare
 */
public class HtmlView<M> extends HtmlPage {

    /** Function that consumes an HtmlView to produce HTML elements. */
    protected final HtmlTemplate template;

    /**
     * This field is like a union with the threadLocalVisitor, being used alternatively. For non
     * thread safe scenarios Visitors maybe shared concurrently by multiple threads. On the
     * other-hand, in thread-safe scenarios each thread must have its own visitor to emit HTML to the
     * output, and we use the threadLocalVisitor field instead.
     */
    private final HtmlVisitor visitor;

    /**
     * This issue is regarding ThreadLocal variables that are supposed to be garbage collected. The
     * given example deals with a static field of ThreadLocal which persists beyond an instance. In
     * this case the ThreadLocal is hold in an instance field and should stay with all thread local
     * instances during its entire life cycle.
     */
    @java.lang.SuppressWarnings("squid:S5164")
    private final ThreadLocal<HtmlVisitor> threadLocalVisitor;

    protected final Supplier<HtmlVisitor> visitorSupplier;
    protected final boolean threadSafe;

    /** Auxiliary constructor used by clone(). */
    HtmlView(
        Supplier<HtmlVisitor> visitorSupplier,
        HtmlTemplate template,
        boolean threadSafe
    ) {
        this.visitorSupplier = visitorSupplier;
        this.template = template;
        this.threadSafe = threadSafe;
        if (threadSafe) {
            this.visitor = null;
            this.threadLocalVisitor = ThreadLocal.withInitial(visitorSupplier);
        } else {
            this.visitor = visitorSupplier.get();
            this.threadLocalVisitor = null;
        }
    }

    public final Html<HtmlPage> html() {
        this.getVisitor().write(HEADER);
        return new Html<>(this);
    }

    public HtmlView<M> threadSafe() {
        return clone(visitorSupplier, true);
    }

    @Override
    public HtmlVisitor getVisitor() {
        return threadSafe ? threadLocalVisitor.get() : visitor;
    }

    public HtmlView<M> setOut(Appendable out) {
        getVisitor().setAppendable(out);
        return this;
    }

    @Override
    public String getName() {
        return "HtmlView";
    }

    public String render() {
        return render(null);
    }

    public String render(M model) {
        StringBuilder str = ((StringBuilder) getVisitor().out());
        str.setLength(0);
        getVisitor().resolve(model);
        return str.toString();
    }

    public void write(M model) {
        getVisitor().resolve(model);
    }

    public void write() {
        write(null);
    }

    /**
     * Since HtmlView is immutable this is the preferred way to create a copy of the existing HtmlView
     * instance with a different threadSafe state.
     *
     * @param visitorSupplier
     * @param threadSafe
     */
    protected HtmlView<M> clone(
        Supplier<HtmlVisitor> visitorSupplier,
        boolean threadSafe
    ) {
        return new HtmlView<>(visitorSupplier, template, threadSafe);
    }

    /**
     * Returns a new instance of HtmlFlow with the same properties of this object but with indented
     * set to the value of isIndented parameter.
     */
    @Override
    public HtmlView<M> setIndented(boolean isIndented) {
        return HtmlFlow.view(
            getVisitor().out(),
            template,
            isIndented,
            threadSafe,
            true
        );
    }

    /**
     * Returns a new instance of HtmlView with the same properties of this object but with preEncoding
     * set to the value of preEncoding parameter.
     *
     * @param preEncoding If true, the view will preEncode static HTML blocks.
     */
    public HtmlView<M> setPreEncoding(boolean preEncoding) {
        return HtmlFlow.view(
            template,
            getVisitor().isIndented,
            threadSafe,
            preEncoding
        );
    }
}
