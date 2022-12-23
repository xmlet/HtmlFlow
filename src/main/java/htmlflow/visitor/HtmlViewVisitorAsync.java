package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationAsyncTerminationNode;
import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;


/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync extends HtmlViewVisitorContinuations implements TagsToAppendable {
    
    private Appendable out;

    /**
     * The last node to be processed.
     */
    protected final HtmlContinuation last;

    public HtmlViewVisitorAsync(boolean isIndented, HtmlContinuation first) {
        super(isIndented, first);
        this.last = findLast();
    }
    
    @Override
    public void write(String text) {
        try {
            out.append(text);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }
    
    @Override
    protected void write(char c) {
        try {
            out.append(c);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }
    
    @Override
    public HtmlVisitor clone(boolean isIndented) {
        return new HtmlViewVisitorAsync(isIndented, first.copy(this));
    }
    
    @Override
    public Appendable out() {
        return this.out;
    }

    @Override
    public void setAppendable(Appendable appendable) {
        this.out = appendable;
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
