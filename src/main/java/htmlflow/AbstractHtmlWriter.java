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
import htmlflow.visitor.HtmlVisitorPrintStreamDynamic;
import htmlflow.visitor.HtmlVisitorStringBuilderDynamic;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Root;
import org.xmlet.htmlapifaster.Tr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;

/**
 * The root container for HTML elements.
 * It it responsible for managing the {@code org.xmlet.htmlapi.ElementVisitor}
 * implementation, which is responsible for printing the tree of elements and
 * attributes.
 *
 * Instances of AbstractHtmlWriter are immutable. Any change to its properties returns a new
 * instance of AbstractHtmlWriter.
 *
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 29-03-2012
 */
public abstract class AbstractHtmlWriter<T> implements HtmlWriter<T>, Element<AbstractHtmlWriter<T>, Element<?,?>> {
    static final String WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS =
            "Cannot use PrintStream output for thread-safe views!";

    static final String WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM =
            "Cannot set thread-safety for views with PrintStream output!";

    private static final String HEADER;
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String HEADER_TEMPLATE = "templates/HtmlView-Header.txt";

    static {
        try {
            URL headerUrl = AbstractHtmlWriter.class
                    .getClassLoader()
                    .getResource(HEADER_TEMPLATE);
            if(headerUrl == null)
                throw new FileNotFoundException(HEADER_TEMPLATE);
            InputStream headerStream = headerUrl.openStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(headerStream))) {
                HEADER = reader.lines().collect(joining(NEWLINE));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * This field is like an union with the threadLocalVisitor, being used alternatively.
     * For non thread safe scenarios Visitors maybe shared concurrently by multiple threads.
     * On the other-hand, in thread-safe scenarios each thread must have its own visitor to
     * throw the output and we use the threadLocalVisitor field instead.
     */
    private final HtmlVisitor visitor;
    /**
     * This issue is regarding ThreadLocal variables that are supposed to be garbage collected.
     * The given example deals with a static field of ThreadLocal which persists beyond an instance.
     * In this case the ThreadLocal is hold in an instance field and should stay with all
     * thread local instances during its entire life cycle.
     */
    @java.lang.SuppressWarnings("squid:S5164")
    private final ThreadLocal<HtmlVisitor> threadLocalVisitor;
    private final Supplier<HtmlVisitor> visitorSupplier;
    private final boolean threadSafe;

    protected AbstractHtmlWriter(Supplier<HtmlVisitor> visitorSupplier, boolean threadSafe) {
        this.visitorSupplier = visitorSupplier;
        this.threadSafe = threadSafe;
        if(threadSafe) {
            this.visitor = null;
            this.threadLocalVisitor = ThreadLocal.withInitial(visitorSupplier);
        } else {
            this.visitor = visitorSupplier.get();
            this.threadLocalVisitor = null;
        }
    }

    public final Html<AbstractHtmlWriter<T>> html() {
        if (this.getVisitor().isWriting())
            this.getVisitor().write(HEADER);
        return new Html<>(this);
    }

    public final Div<AbstractHtmlWriter<T>> div() {
        return new Div<>(this);
    }

    public final Tr<AbstractHtmlWriter<T>> tr() {
        return new Tr<>(this);
    }

    public final Root<AbstractHtmlWriter<T>> defineRoot(){
        return new Root<>(this);
    }

    /**
     * Returns a new instance of AbstractHtmlWriter with the same properties of this object
     * but with a new HtmlVisitorCache set with the out PrintStream parameter.
     */
    @Override
    public final HtmlWriter<T> setPrintStream(PrintStream out) {
        if(threadSafe)
            throw new IllegalArgumentException(WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS);
        Supplier<HtmlVisitor> v = out == null
            ? () -> new HtmlVisitorStringBuilderDynamic(true)
            : () -> new HtmlVisitorPrintStreamDynamic(out, true);
        return clone(v, false);
    }

    /**
     * Returns a new instance of AbstractHtmlWriter with the same properties of this object
     * but with indented set to the value of isIndented parameter.
     */
    public final AbstractHtmlWriter<T> setIndented(boolean isIndented) {
        return clone(() -> getVisitor().clone(isIndented), false);
    }

    @Override
    public final AbstractHtmlWriter<T> self() {
        return this;
    }

    public final AbstractHtmlWriter<T> threadSafe(){
        /**
         * I don't like this kind of verification.
         * Yet, we need to keep backward compatibility with views based
         * on PrintStream output, which are not viable in a multi-thread scenario.
         */
        if(getVisitor() instanceof HtmlVisitorPrintStreamDynamic) {
            throw new IllegalStateException(WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM);
        }
        return clone(visitorSupplier, true);
    }

    @Override
    public HtmlVisitor getVisitor() {
        return threadSafe
            ? threadLocalVisitor.get()
            : visitor;
    }

    @Override
    public Element __() {
        throw new IllegalStateException(getName() + " is the root of Html tree and it has not any parent.");
    }

    @Override
    public Element getParent() {
        throw new IllegalStateException(getName() + " is the root of Html tree and it has not any parent.");
    }

    /**
     * Adds a partial view to this view.
     *
     * @param partial inner view.
     * @param model the domain object bound to the partial view.
     * @param <U> the type of the domain model of the partial view.
     */
    public final <U> void addPartial(AbstractHtmlWriter<U> partial, U model) {
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
            HtmlVisitor v = getVisitor();
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
    public final <U> void addPartial(AbstractHtmlWriter<U> partial) {
        getVisitor().closeBeginTag();
        partial.getVisitor().setDepth(getVisitor().getDepth());
        if (this.getVisitor().isWriting()) {
            /**
             * This overloaded addPartial does not have a model object.
             * Thus we do not expect to incur in the same problem reported on the other addPartial(partial, model).
             * Moreover partials are HTML fragments that are not expect to incur in more than Kb.
             */
            String p = partial.render();
            if(p != null) getVisitor().write(p);
        }
    }

    /**
     * Since AbstractHtmlWriter is immutable this is the preferred way to create a copy of the
     * existing AbstractHtmlWriter instance with a different threadSafe state.
     *
     * @param visitorSupplier
     * @param threadSafe
     */
    protected abstract AbstractHtmlWriter<T> clone(Supplier<HtmlVisitor> visitorSupplier, boolean threadSafe);

    /**
     * Resulting in a non thread safe view.
     * Receives an existent visitor.
     * Usually for a parent view to share its visitor with a partial.
     */
    protected abstract AbstractHtmlWriter<T> clone(HtmlVisitor visitor);
}
