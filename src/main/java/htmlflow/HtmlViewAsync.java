package htmlflow;

import htmlflow.visitor.HtmlViewVisitor;
import htmlflow.visitor.HtmlVisitorAsync;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.Html;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class HtmlViewAsync<T> extends HtmlPage<T> {
    
    private static final String WRONG_USE_OF_RENDER_WITHOUT_MODEL =
            "Wrong use of HtmlViewAsync! You should provide a " +
                    "model parameter or use a static view instead!";
    
    private static final String WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR =
            "Wrong use of HtmlViewAsync writeAsync! You should use the viewAsync creation instead!";
    
    private static final String WRONG_USE_OF_RENDER_WITH_ASYNC_VIEW =
            "Wrong use of HtmlViewAsync! You should use the writeAsync method instead";
    
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
     * Used alternately with the field template.
     * A binder function is responsible for binding the View with a domain object.
     * Thus, it is a function that receives two arguments: the view and the domain object.
     */
    private final BiConsumer<HtmlViewAsync<T>, T> binder;
    
    /**
     * To check whether this view is emitting to PrintStream, or not.
     * Notice since the PrintStream maybe shared by different views processing
     * we cannot ensure thread safety, because concurrent threads maybe emitting
     * different HTML to the same PrintStream.
     */
    private final PrintStream out;
    
    /**
     * Auxiliary constructor used by clone().
     *
     * @param out
     * @param visitorSupplier
     * @param threadSafe
     * @param binder
     */
    HtmlViewAsync(PrintStream out, Supplier<HtmlViewVisitor> visitorSupplier,
                  boolean threadSafe,
                  BiConsumer<HtmlViewAsync<T>, T> binder) {
        this.out = out;
        this.visitorSupplier = visitorSupplier;
        this.threadSafe = threadSafe;
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
        final HtmlViewVisitor localVisitor = this.getVisitor();
    
        if (!(localVisitor instanceof HtmlVisitorAsync)) {
            throw new UnsupportedOperationException(WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR);
        }
    
        HtmlVisitorAsync visitorAsync = (HtmlVisitorAsync) localVisitor;
        binder.accept(this, model);
        return visitorAsync.finishedAsync();
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
        return new HtmlViewAsync<>(out, visitorSupplier, threadSafe, binder);
    }
    
    /**
     * Resulting in a non thread safe view.
     * Receives an existent visitor.
     * Usually for a parent view to share its visitor with a partial.
     */
    protected HtmlPage<T> clone(HtmlViewVisitor visitor) {
        return new HtmlViewAsync<>(out, () -> visitor, false, binder);
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
