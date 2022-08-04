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

import htmlflow.visitor.HtmlVisitorCache;
import htmlflow.visitor.HtmlVisitorPrintStream;
import htmlflow.visitor.HtmlVisitorStringBuilder;
import org.xmlet.htmlapifaster.*;

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
 * Instances of HtmlView are immutable. Any change to its properties returns a new
 * instance of HtmlView.
 *
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 29-03-2012
 */
public abstract class HtmlView<T> implements HtmlWriter<T>, Element<HtmlView<T>, Element<?,?>> {
    static final String WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS =
            "Cannot use PrintStream output for thread-safe views!";

    static final String WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM =
            "Cannot set thread-safety for views with PrintStream output!";

    private static final String HEADER;
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String HEADER_TEMPLATE = "templates/HtmlView-Header.txt";

    static {
        try {
            URL headerUrl = HtmlView.class
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
    private final HtmlVisitorCache visitor;
    /**
     * This issue is regarding ThreadLocal variables that are supposed to be garbage collected.
     * The given example deals with a static field of ThreadLocal which persists beyond an instance.
     * In this case the ThreadLocal is hold in an instance field and should stay with all
     * thread local instances during its entire life cycle.
     */
    @java.lang.SuppressWarnings("squid:S5164")
    private final ThreadLocal<HtmlVisitorCache> threadLocalVisitor;
    private final Supplier<HtmlVisitorCache> visitorSupplier;
    private final boolean threadSafe;

    protected HtmlView(Supplier<HtmlVisitorCache> visitorSupplier, boolean threadSafe) {
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

    public final Html<HtmlView<T>> html() {
        if (this.getVisitor().isWriting())
            this.getVisitor().write(HEADER);
        return new Html<>(this);
    }

    public final Div<HtmlView<T>> div() {
        return new Div<>(this);
    }

    public final Tr<HtmlView<T>> tr() {
        return new Tr<>(this);
    }

    public final Root<HtmlView<T>> defineRoot(){
        return new Root<>(this);
    }

    /**
     * Returns a new instance of HtmlView with the same properties of this object
     * but with a new HtmlVisitorCache set with the out PrintStream parameter.
     */
    @Override
    public final HtmlWriter<T> setPrintStream(PrintStream out) {
        if(threadSafe)
            throw new IllegalArgumentException(WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS);
        Supplier<HtmlVisitorCache> v = out == null
            ? () -> new HtmlVisitorStringBuilder(getVisitor().isDynamic)
            : () -> new HtmlVisitorPrintStream(out, getVisitor().isDynamic);
        return clone(v, false);
    }

    /**
     * Returns a new instance of HtmlView with the same properties of this object
     * but with indented set to the value of isIndented parameter.
     */
    public final HtmlView<T> setIndented(boolean isIndented) {
        return clone(() -> getVisitor()
            .clone(isIndented), false);
    }

    @Override
    public final HtmlView<T> self() {
        return this;
    }

    public final HtmlView<T> threadSafe(){
        /**
         * I don't like this kind of verification.
         * Yet, we need to keep backward compatibility with views based
         * on PrintStream output, which are not viable in a multi-thread scenario.
         */
        if(getVisitor() instanceof HtmlVisitorPrintStream) {
            throw new IllegalStateException(WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM);
        }
        return clone(visitorSupplier, true);
    }

    @Override
    public final HtmlVisitorCache getVisitor() {
        return threadSafe
            ? threadLocalVisitor.get()
            : visitor;
    }

    @Override
    public String getName() {
        return "HtmlView";
    }

    @Override
    public Element __() {
        throw new IllegalStateException("HtmlView is the root of Html tree and it has not any parent.");
    }

    @Override
    public Element getParent() {
        throw new IllegalStateException("HtmlView is the root of Html tree and it has not any parent.");
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
            HtmlVisitorCache v = getVisitor();
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
            String p = partial.render();
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
    protected abstract HtmlView<T> clone(Supplier<HtmlVisitorCache> visitorSupplier, boolean threadSafe);

    /**
     * Resulting in a non thread safe view.
     * Receives an existent visitor.
     * Usually for a parent view to share its visitor with a partial.
     */
    protected abstract HtmlView<T> clone(HtmlVisitorCache visitor);
}
