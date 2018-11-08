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
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 29-03-2012
 */
public abstract class HtmlView<T> implements HtmlWriter<T>, Element<HtmlView, Element> {
    static final String WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS =
            "Cannot use PrintStream output for thread-safe views!";

    static final String WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM =
            "Cannot set thread-safety for views with PrintStream output!";

    static final String WRONG_USE_OF_RENDER_WITH_PRINTSTREAM =
            "Wrong use of render(). " +
            "Use write() rather than render() to output to PrintStream. " +
            "To get a String from render() you must use view() without a PrintStream ";


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

    private HtmlVisitorCache visitor;
    private ThreadLocal<HtmlVisitorCache> threadLocalVisitor;
    private Supplier<HtmlVisitorCache> visitorSupplier;
    private boolean threadSafe = false;

    public Html<HtmlView> html() {
        if (this.getVisitor().isWriting())
            this.getVisitor().write(HEADER);
        return new Html<>(this);
    }

    public Div<HtmlView> div() {
        return new Div<>(this);
    }

    public Tr<HtmlView> tr() {
        return new Tr<>(this);
    }

    public Root<HtmlView> defineRoot(){
        return new Root<>(this);
    }

    @Override
    public HtmlWriter<T> setPrintStream(PrintStream out) {
        if(threadSafe)
            throw new IllegalArgumentException(WRONG_USE_OF_PRINTSTREAM_ON_THREADSAFE_VIEWS);
        Supplier<HtmlVisitorCache> v = out == null
            ? () -> new HtmlVisitorStringBuilder(getVisitor().isDynamic)
            : () -> new HtmlVisitorPrintStream(out, getVisitor().isDynamic);
        setVisitor(v);
        return this;
    }

    @Override
    public HtmlView<T> self() {
        return this;
    }

    public HtmlView<T> threadSafe(){
        /**
         * I don't like this kind of verification.
         * Yet, we need to keep backward compatibility with views based
         * on PrintStream output, which are not viable in a multi-thread scenario.
         */
        if(getVisitor() instanceof HtmlVisitorPrintStream) {
            throw new IllegalStateException(WRONG_USE_OF_THREADSAFE_ON_VIEWS_WITH_PRINTSTREAM);
        }
        this.threadSafe = true;
        setVisitor(visitorSupplier);
        return this;
    }

    @Override
    public HtmlVisitorCache getVisitor() {
        return threadSafe
            ? threadLocalVisitor.get()
            : visitor;
    }

    public void setVisitor(Supplier<HtmlVisitorCache> visitor) {
        visitorSupplier = visitor;
        if(threadSafe) {
            this.visitor = null;
            this.threadLocalVisitor = ThreadLocal.withInitial(visitor);
        } else {
            this.visitor = visitor.get();
            this.threadLocalVisitor = null;
        }
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
        partial.getVisitor().depth = getVisitor().depth;
        if (this.getVisitor().isWriting())
            getVisitor().write(partial.render(model));
    }

    /**
     * Adds a partial view to this view.
     *
     * @param partial inner view.
     * @param <U> the type of the domain model of the partial view.
     */
    public final <U> void addPartial(HtmlView<U> partial) {
        getVisitor().closeBeginTag();
        partial.getVisitor().depth = getVisitor().depth;
        if (this.getVisitor().isWriting())
            getVisitor().write(partial.render());
    }
}
