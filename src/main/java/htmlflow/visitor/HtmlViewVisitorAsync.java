package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationAsyncTerminationNode;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;


/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync extends HtmlVisitorAsync {

    /**
     * The first node to be processed.
     */
    protected final HtmlContinuation first;
    /**
     * The last node to be processed.
     */
    protected final HtmlContinuation last;

    public HtmlViewVisitorAsync(boolean isIndented, HtmlContinuation first) {
        /**
         * Intentionally pass null to out Appendable.
         * Since this visitor allows concurrency, due to its asynchronous nature, then we must
         * clone it on each resolution (i.e. finishAsync()) to avoid sharing continuations across
         * different tasks and set a new out Appendable.
         */
        this(null, isIndented, first);
    }

    /**
     * Auxiliary for clone.
     */
    private HtmlViewVisitorAsync(Appendable out, boolean isIndented, HtmlContinuation first) {
        super(out, isIndented);
        this.first = first.copy(this);
        this.last = findLast();
    }

    @Override
    public void resolve(Object model) {
        first.execute(model);
    }

    @Override
    public HtmlViewVisitorAsync clone(boolean isIndented) {
        return new HtmlViewVisitorAsync(out, isIndented, first);
    }

    @Override
    public HtmlViewVisitorAsync clone(Appendable out) {
        return new HtmlViewVisitorAsync(out, isIndented, first);
    }

    public CompletableFuture<Void> finishedAsync(Object model) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        HtmlContinuationAsyncTerminationNode terminationNode = new HtmlContinuationAsyncTerminationNode(cf);
        /**
         * Chain terminationNode next to the last node.
         * Keep last pointing to the same node to replace the terminationNode on
         * others render async.
         */
        setNext(last, terminationNode);
        /**
         * Initializes render on first node.
         */
        this.first.execute(model);

        return cf;
    }

    private HtmlContinuation findLast() {
        HtmlContinuation node = this.first;

        while (node.next != null){
            node = node.next;
        }

        return node;
    }

    @Override
    public final <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> dynamicHtmlBlock) {
        throw new IllegalStateException("Wrong use of visitDynamic() in a HtmlViewAsync! Preprocessing visitor should have already visited");
    }

    @Override
    public <M, E extends Element> void visitAwait(E element, AwaitConsumer<E,M> asyncAction) {
        throw new IllegalStateException("Wrong use of visitAwait() in a HtmlViewAsync! Preprocessing visitor should have already visited");
    }

    @Override
    public <M, E extends Element> void visitSuspending(E element, SuspendConsumer<E, M> suspendAction) {
        throw new IllegalStateException("Wrong use of suspending() in a HtmlViewAsync! Use HtmlFlow.viewSuspend() to produce a suspendable view.");
    }
}
