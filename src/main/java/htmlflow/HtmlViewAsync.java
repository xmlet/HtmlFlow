package htmlflow;

import htmlflow.visitor.HtmlVisitor;
import htmlflow.visitor.HtmlVisitorAsync;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

//TODO extends from HtmlView
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
     * @param binder
     */
    HtmlViewAsync(PrintStream out, Supplier<HtmlVisitor> visitorSupplier,
                  boolean threadSafe,
                  HtmlTemplate<T> template,
                  BiConsumer<HtmlView<T>, T> binder) {
        super(out, visitorSupplier, threadSafe, template, binder);
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
    
    public String render(T model, HtmlView...partials) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITH_ASYNC_VIEW);
    }
    
    public final void write(T model, HtmlView...partials) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITHOUT_MODEL);
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
    
        if (!(localVisitor instanceof HtmlVisitorAsync)) {
            throw new UnsupportedOperationException(WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR);
        }
    
        HtmlVisitorAsync visitorAsync = (HtmlVisitorAsync) localVisitor;
        this.binder.accept(this, model);
        return visitorAsync.finishedAsync();
    }
    
    public final CompletableFuture<Void> writeAsync(T model, HtmlView... partials) {
        final HtmlVisitor localVisitor = this.getVisitor();
    
        if (!(localVisitor instanceof HtmlVisitorAsync)) {
            throw new UnsupportedOperationException(WRONG_USE_OF_WRITE_ASYNC_WITHOUT_ASYNC_VISITOR);
        }
    
        HtmlVisitorAsync visitorAsync = (HtmlVisitorAsync) localVisitor;
        template.resolve(this, model, partials);
        return visitorAsync.finishedAsync();
    }
}
