package htmlflow.visitor;

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Hot reload visitor that processes templates without preprocessing optimization.
 * This visitor recalculates the entire HTML on every rendering, making it suitable
 * for development scenarios where template changes need immediate reflection.
 *
 * <p><strong>Limitations:</strong>
 * <ul>
 *   <li>Does not support suspending operations (visitSuspending)</li>
 *   <li>Performance is significantly slower than optimized visitor</li>
 *   <li>No preprocessing - direct template execution</li>
 * </ul>
 *
 * @see HtmlViewVisitorAsync for optimized visitor with preprocessing
 */
public class HtmlViewVisitorAsyncHot extends HtmlVisitorAsync {

    private Object model;

    public HtmlViewVisitorAsyncHot(boolean isIndented) {
        super(null, isIndented);
    }

    /**
     * Auxiliary for clone.
     */
    private HtmlViewVisitorAsyncHot(Appendable out, boolean isIndented) {
        super(out, isIndented);
    }

    @Override
    public void resolve(Object model) {
        this.model = model;
    }

    @Override
    public HtmlVisitorAsync clone(boolean isIndented) {
        return new HtmlViewVisitorAsyncHot(out, isIndented);
    }

    public HtmlVisitorAsync clone(Appendable out) {
        return new HtmlViewVisitorAsyncHot(isIndented);
    }

    @Override
    public <E extends Element, U> void visitDynamic(E e, BiConsumer<E, U> biConsumer) {
        biConsumer.accept(e, (U) model);
    }

    @Override
    public <M, E extends Element> void visitAwait(E e, AwaitConsumer<E, M> awaitConsumer) {
        CompletableFuture <Void> future = new CompletableFuture<>();
        awaitConsumer.accept(e, (M) model, () -> {
            future.complete(null);
        });
        // Wait for the consumer to complete so that the HTML can be generated.
        // This is done to ensure that the HTML is generated sequentially and to avoid
        // malformed HTML due to concurrent processing.
        future.join();
    }

    @Override
    public <M, E extends Element> void visitSuspending(E element, SuspendConsumer<E, M> suspendAction) {
        throw new UnsupportedOperationException("Hot reload visitor does not support suspending operations. Use HtmlViewVisitorAsync instead.");
    }

    /**
     * Returns a CompletableFuture that completes immediately since hot visitor
     * executes everything synchronously during template processing.
     * The async operations are handled directly by the AwaitConsumer callbacks.
     */
    @Override
    public CompletableFuture<Void> finishedAsync(Object model) {
        this.model = model;
        
        return CompletableFuture.completedFuture(null);
    }
}
