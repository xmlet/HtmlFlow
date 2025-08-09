package htmlflow;

import htmlflow.visitor.HtmlVisitorAsync;

import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous hot reload views can be bound to a domain object with an asynchronous API.
 * Dynamic views are unoptimized and do not store static HTML blocks, recalculating HTML on every rendering.
 * This makes them suitable for development scenarios where template changes need immediate reflection.
 *
 * <p><strong>Features:</strong>
 * <ul>
 *   <li>Supports async operations (visitAwait)</li>
 *   <li>Does not support suspending operations (visitSuspending)</li>
 *   <li>Performance is significantly slower than optimized visitor</li>
 *   <li>No preprocessing - direct template execution</li>
 * </ul>
 *
 * @param <M> Type of the model rendered with this view.
 *
 * @author Bernardo Pereira
 */
public class HtmlViewAsyncHot<M> extends HtmlViewAsync<M> {
    /**
     * Auxiliary constructor used by clone().
     *
     * @param visitorSupplier
     * @param template
     * @param threadSafe
     */
    HtmlViewAsyncHot(HtmlVisitorAsync visitor, HtmlTemplate template, boolean threadSafe) {
        super(visitor, template, threadSafe);
    }

    @Override
    public String getName() {
        return "HtmlViewAsyncHot";
    }

    public final CompletableFuture<Void> writeAsync(Appendable out, M model) {
        HtmlVisitorAsync visitor = getVisitor();
        if (threadSafe) {
            visitor = visitor.clone(out);
        } else {
            visitor.setAppendable(out);
        }
        visitor.resolve(model);
        this.template.resolve(this);
        return visitor.finishedAsync(model);
    }

    @Override
    public HtmlViewAsync<M> threadSafe(){
        return new HtmlViewAsyncHot<>(visitor, template, true);
    }

    @Override
    public HtmlViewAsync<M> threadUnsafe(){
        return new HtmlViewAsyncHot<>(visitor, template, false);
    }

    @Override
    public HtmlViewAsync<M> setIndented(boolean isIndented) {
        return HtmlFlow.viewAsync(template, isIndented, threadSafe, false);
    }
}
