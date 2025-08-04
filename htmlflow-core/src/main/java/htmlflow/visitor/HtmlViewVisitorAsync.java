package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationAsyncTerminationNode;

import java.util.concurrent.CompletableFuture;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;


/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync extends HtmlViewVisitor {
    
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
    private HtmlViewVisitorAsync(Appendable out, boolean isIndented, HtmlContinuation copy) {
        super(out, isIndented, copy);
        this.last = findLast();
    }

    @Override
    public HtmlViewVisitorAsync clone(boolean isIndented) {
        return new HtmlViewVisitorAsync(out, isIndented, first);
    }

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
}
