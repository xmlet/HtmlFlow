package htmlflow;

import htmlflow.visitor.HtmlVisitorAsync;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous hot reload views can be bound to a domain object with an asynchronous API. Dynamic
 * views are unoptimized and do not store static HTML blocks, recalculating HTML on every rendering.
 * This makes them suitable for development scenarios where template changes need immediate
 * reflection.
 *
 * <p><strong>Features:</strong>
 *
 * <ul>
 *   <li>Supports async operations (visitAwait)
 *   <li>Does not support suspending operations (visitSuspending)
 *   <li>Performance is significantly slower than optimized visitor
 *   <li>No preprocessing - direct template execution
 * </ul>
 *
 * @param <M> Type of the model rendered with this view.
 * @author Bernardo Pereira
 */
public class HtmlViewAsyncHot<M> extends HtmlViewAsync<M> {

    /**
     * Auxiliary constructor used by clone().
     *
     * @param template the HTML template to be used for rendering
     * @param threadSafe indicates whether the view should be thread-safe
     */
    HtmlViewAsyncHot(
        HtmlVisitorAsync visitor,
        HtmlTemplate template,
        boolean threadSafe
    ) {
        super(visitor, template, threadSafe);
    }

    @Override
    public String getName() {
        return "HtmlViewAsyncHot";
    }

    @Override
    public final CompletableFuture<Void> writeAsync(Appendable out, M model) {
        HtmlVisitorAsync visitor = getVisitor();
        if (threadSafe) {
            visitor = visitor.clone(out);
        } else {
            visitor.setAppendable(out);
        }
        return visitor.finishedAsync(model);
    }

    @Override
    public HtmlViewAsyncHot<M> threadSafe() {
        return new HtmlViewAsyncHot<>(visitor, template, true);
    }

    @Override
    public HtmlViewAsyncHot<M> threadUnsafe() {
        return new HtmlViewAsyncHot<>(visitor, template, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HtmlViewAsyncHot<M> setIndented(boolean isIndented) {
        return (HtmlViewAsyncHot<M>) HtmlFlow.viewAsync(
            template,
            isIndented,
            threadSafe,
            false
        );
    }

    @Override
    public final CompletableFuture<String> renderAsync(M model) {
        StringBuilder str = new StringBuilder();
        return writeAsync(str, model)
            .thenApply(nothing -> str.toString().trim());
    }
}
