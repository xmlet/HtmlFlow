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

    public HtmlViewVisitorAsync(Appendable out, boolean isIndented, HtmlContinuation first) {
        super(out, isIndented, first);
        this.last = findLast();
    }
    
    @Override
    public HtmlVisitor clone(boolean isIndented) {
        return new HtmlViewVisitorAsync(out, isIndented, first.copy(this));
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
