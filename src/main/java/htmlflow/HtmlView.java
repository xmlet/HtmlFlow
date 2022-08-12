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

import htmlflow.visitor.HtmlViewVisitor;
import org.xmlet.htmlapifaster.Html;

import java.io.PrintStream;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Dynamic views can be bound to a domain object.
 *
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa, Luís Duare
 */
public class HtmlView<T> extends HtmlPage<T> {

    private static final String WRONG_USE_OF_RENDER_WITHOUT_MODEL =
             "Wrong use of HtmlView! You should provide a " +
             "model parameter or use a static view instead!";
    /**
     * This field is like an union with the threadLocalVisitor, being used alternatively.
     * For non thread safe scenarios Visitors maybe shared concurrently by multiple threads.
     * On the other-hand, in thread-safe scenarios each thread must have its own visitor to
     * throw the output and we use the threadLocalVisitor field instead.
     */
    private final HtmlViewVisitor visitor;
    /**
     * This issue is regarding ThreadLocal variables that are supposed to be garbage collected.
     * The given example deals with a static field of ThreadLocal which persists beyond an instance.
     * In this case the ThreadLocal is hold in an instance field and should stay with all
     * thread local instances during its entire life cycle.
     */
    @java.lang.SuppressWarnings("squid:S5164")
    private final ThreadLocal<HtmlViewVisitor> threadLocalVisitor;
    private final Supplier<HtmlViewVisitor> visitorSupplier;
    private final boolean threadSafe;

    /**
     * Used alternately with the field binder.
     * A template function receives 3 arguments:
     *   the view, the domain object and a varargs array of partial views.
     */
    private final HtmlTemplate<T> template;
    /**
     * Used alternately with the field template.
     * A binder function is responsible for binding the View with a domain object.
     * Thus, it is a function that receives two arguments: the view and the domain object.
     */
    private final BiConsumer<HtmlView<T>, T> binder;

    /**
     * To check whether this view is emitting to PrintStream, or not.
     * Notice since the PrintStream maybe shared by different views processing
     * we cannot ensure thread safety, because concurrent threads maybe emitting
     * different HTML to the same PrintStream.
     */
    private final PrintStream out;
    /**
     * Auxiliary constructor used by clone().
     */
    HtmlView(
        PrintStream out,
        Supplier<HtmlViewVisitor> visitorSupplier,
        boolean threadSafe,
        HtmlTemplate<T> template,
        BiConsumer<HtmlView<T>, T> binder)
    {
        this.out = out;
        this.visitorSupplier = visitorSupplier;
        this.threadSafe = threadSafe;
        this.template = template;
        this.binder = binder;
        if(threadSafe) {
            this.visitor = null;
            this.threadLocalVisitor = ThreadLocal.withInitial(visitorSupplier);
        } else {
            this.visitor = visitorSupplier.get();
            this.threadLocalVisitor = null;
        }
    }

    public final Html<HtmlPage<T>> html() {
        if (this.getVisitor().isWriting())
            this.getVisitor().write(HEADER);
        return new Html<>(this);
    }

    /**
     * Returns a new instance of AbstractHtmlWriter with the same properties of this object
     * but with a new HtmlVisitor set with the out PrintStream parameter.
     */
    @Override
    public final HtmlWriter<T> setPrintStream(PrintStream out) {
        if(threadSafe)
            throw new IllegalArgumentException(WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS);
        HtmlViewVisitor v = getVisitor();
        return clone(() -> v.clone(out, v.isIndented), false);
    }

    public final HtmlPage<T> threadSafe(){
        /**
         * PrintStream output is not viable in a multi-thread scenario,
         * because different Visitor instances may share the same PrintStream.
         */
        if(out != null) {
            throw new IllegalStateException(WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM);
        }
        return clone(visitorSupplier, true);
    }

    @Override
    public HtmlViewVisitor getVisitor() {
        return threadSafe
            ? threadLocalVisitor.get()
            : visitor;
    }

    @Override
    public String getName() {
        return "HtmlView";
    }

    @Override
    public final String render() {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }

    @Override
    public final String render(T model) {
        binder.accept(this, model);
        return getVisitor().finished();
    }

    public final String render(T model, HtmlView...partials) {
        template.resolve(this, model, partials);
        return getVisitor().finished();
    }

    @Override
    public final void write() {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
    }

    @Override
    public final void write(T model) {
        this.render(model);
    }

    public final void write(T model, HtmlView...partials) {
        this.render(model, partials);
    }

        /**
     * Adds a partial view to this view.
     *
     * @param partial inner view.
     * @param model the domain object bound to the partial view.
     * @param <U> the type of the domain model of the partial view.
     */
    public final <U> void addPartial(HtmlView<U> partial, U model) {
        getVisitor().closeBeginTag();
        partial.getVisitor().setDepth(getVisitor().getDepth());
        if (this.getVisitor().isWriting()) {
            /**
             * Next partial.clone() is related with https://github.com/xmlet/HtmlFlow/issues/75
             * The call to render() returns null or an HTML string depending on whether it has
             * a HtmlVisitorPrintStream or a HtmlVisitorStringBuilder.
             * The latter incurs in additional overheads due to internal string buffering and may
             * throw OutOfMemoryError for large data model.
             * Thus we can avoid that problem sharing the parent view's visitor with the partial.
             * Once the visitor is stored in a final field, we must clone the partial view.
             * Since our views and templates are data-structure less avoiding in memory trees and nodes,
             * and the HTML document is based on a higher-order function, then our views instances only store
             * a few instance fields related to the visitor and the template function itself.
             */
            HtmlViewVisitor v = getVisitor();
            String p = partial.clone(v.newbie()).render(model);
            if(p != null) v.write(p);
        }
    }

    /**
     * Adds a partial view to this view.
     *
     * @param partial inner view.
     * @param <U> the type of the domain model of the partial view.
     */
    public final <U> void addPartial(HtmlView<U> partial) {
        getVisitor().closeBeginTag();
        partial.getVisitor().setDepth(getVisitor().getDepth());
        if (this.getVisitor().isWriting()) {
            /**
             * This overloaded addPartial does not have a model object.
             * Thus we do not expect to incur in the same problem reported on the other addPartial(partial, model).
             * Moreover partials are HTML fragments that are not expect to incur in more than Kb.
             */
            String p = partial.render(null);
            if(p != null) getVisitor().write(p);
        }
    }

    /**
     * Since HtmlView is immutable this is the preferred way to create a copy of the
     * existing HtmlView instance with a different threadSafe state.
     *
     * @param visitorSupplier
     * @param threadSafe
     */
    protected final HtmlPage<T> clone(
        Supplier<HtmlViewVisitor> visitorSupplier,
        boolean threadSafe)
    {
        return new HtmlView<>(out, visitorSupplier, threadSafe, template, binder);
    }

    /**
     * Resulting in a non thread safe view.
     * Receives an existent visitor.
     * Usually for a parent view to share its visitor with a partial.
     */
    protected HtmlPage<T> clone(HtmlViewVisitor visitor) {
        return new HtmlView<>(out, () -> visitor, false, template, binder);
    }

    /**
     * Returns a new instance of HtmlFlow with the same properties of this object
     * but with indented set to the value of isIndented parameter.
     */
    @Override
    public final HtmlPage<T> setIndented(boolean isIndented) {
        return clone(() -> getVisitor().clone(out, isIndented), false);
    }
}
