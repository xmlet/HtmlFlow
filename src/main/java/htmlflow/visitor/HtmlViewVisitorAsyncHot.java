package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuationAsyncTerminationNode;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;

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

    private final Supplier<PreprocessingVisitorAsync> continuationSupplier;

    public HtmlViewVisitorAsyncHot(boolean isIndented, Supplier<PreprocessingVisitorAsync> continuationSupplier) {
        /**
         * Intentionally pass null to out Appendable.
         * Since this visitor allows concurrency, due to its asynchronous nature, then we must
         * clone it on each resolution (i.e. finishAsync()) to avoid sharing continuations across
         * different tasks and set a new out Appendable.
         */
        this(null, isIndented, continuationSupplier);
    }

    /**
     * Auxiliary for clone.
     */
    private HtmlViewVisitorAsyncHot(Appendable out, boolean isIndented, Supplier<PreprocessingVisitorAsync> continuationSupplier) {
        super(out, isIndented);
        this.continuationSupplier = continuationSupplier;
    }

    @Override
    public void resolve(Object model) {
        // no-op
    }

    @Override
    public HtmlVisitorAsync clone(boolean isIndented) {
        return new HtmlViewVisitorAsyncHot(out, isIndented, continuationSupplier);
    }

    @Override
    public HtmlVisitorAsync clone(Appendable out) {
        return new HtmlViewVisitorAsyncHot(out, isIndented, continuationSupplier);
    }

    /**
     * In order to create well-formed HTML as well as enable progressive rendering (not blocking)
     * the thread in the async consumer), we need to use continuations.
     * <p>
     * As such, this uses PreprocessingVisitorAsync to create a chain of continuations that will be created and
     * executed on each rendering of the template.
     *
     * @param model
     */
    @Override
    public CompletableFuture<Void> finishedAsync(Object model) {
        PreprocessingVisitorAsync continuationProcessor = continuationSupplier.get();

        CompletableFuture<Void> cf = new CompletableFuture<>();
        HtmlContinuationAsyncTerminationNode terminationNode = new HtmlContinuationAsyncTerminationNode(cf);
        PreprocessingVisitor.HtmlContinuationSetter.setNext(continuationProcessor.findLast(), terminationNode);

        continuationProcessor.setAppendable(out);
        continuationProcessor.getFirst().execute(model);

        return cf;
    }

    @Override
    public <E extends Element, U> void visitDynamic(E e, BiConsumer<E, U> biConsumer) {
        throw new IllegalStateException("Invalid use of visitDynamic() in HtmlViewVisitorAsyncHot! " +
                "This should have been called in the continuation processor (PreprocessingVisitorAsync) ");
    }

    @Override
    public <M, E extends Element> void visitAwait(E e, AwaitConsumer<E, M> awaitConsumer) {
        throw new IllegalStateException("Invalid use of visitAwait() in HtmlViewVisitorAsyncHot! " +
                "This should have been called in the continuation processor (PreprocessingVisitorAsync)");
        // 1. This would cause malformed HTML due to the next visits emitting HTML
        // before this consumer is completed.
        // awaitConsumer.accept(e, null, () -> {
        //
        // });

        // 2. This would create well-formed HTML, but it would block the thread until the consumer is completed.
        // Which defeats the purpose of async processing. It would also prevent progressive rendering.
        // CompletableFuture<Void> cf = new CompletableFuture<>();
        // awaitConsumer.accept(e, null, () -> {
        //    cf.complete(null);
        // });
        // cf.join();
    }

    @Override
    public <M, E extends Element> void visitSuspending(E element, SuspendConsumer<E, M> suspendAction) {
        throw new UnsupportedOperationException("Suspending operations are not supported in HtmlViewVisitorAsyncHot.");
    }
}
