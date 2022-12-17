package htmlflow.visitor;

import htmlflow.async.TerminationHtmlContinuationNode;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static htmlflow.visitor.PreprocessingVisitorAsync.HtmlContinuationSetter.setNext;

/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync extends HtmlViewVisitorContinuations implements TagsToPrintStream {
    
    private final PrintStream out;

    /**
     * The last node to be processed.
     */
    protected final HtmlContinuation last;

    public HtmlViewVisitorAsync(PrintStream out, boolean isIndented, HtmlContinuation first) {
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
        out.print(text);
    }
    
    @Override
    protected void write(char c) {
        out.print(c);
    }
    
    @Override
    public HtmlVisitor clone(PrintStream out, boolean isIndented) {
        return new HtmlViewVisitorAsync(out, isIndented, first.copy(this));
    }
    
    @Override
    public PrintStream out() {
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
