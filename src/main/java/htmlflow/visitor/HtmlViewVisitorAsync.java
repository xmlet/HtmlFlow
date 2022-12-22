package htmlflow.visitor;

import htmlflow.async.TerminationHtmlContinuationNode;
import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static htmlflow.visitor.PreprocessingVisitorAsync.HtmlContinuationSetter.setNext;

/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync extends HtmlViewVisitorContinuations implements TagsToAppendable {
    
    private final Appendable out;

    /**
     * The last node to be processed.
     */
    protected final HtmlContinuation last;

    public HtmlViewVisitorAsync(Appendable out, boolean isIndented, HtmlContinuation first) {
        super(isIndented, first);
        this.last = findLast();
        this.out = out;
    }
    
    @Override
    protected String readAndReset() {
        return null;
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
    public HtmlVisitor clone(Appendable out, boolean isIndented) {
        return new HtmlViewVisitorAsync(out, isIndented, first.copy(this));
    }
    
    @Override
    public Appendable out() {
        return this.out;
    }
    
    public CompletableFuture<Void> finishedAsync(Object model) {
        CompletableFuture<Void> cf = new CompletableFuture<>();
        TerminationHtmlContinuationNode terminationNode = new TerminationHtmlContinuationNode(cf);
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
