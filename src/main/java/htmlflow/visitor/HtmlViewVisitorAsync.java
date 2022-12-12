package htmlflow.visitor;

import htmlflow.async.TerminationHtmlContinuationNode;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;

import static htmlflow.visitor.PreprocessingVisitorAsync.HtmlContinuationSetter.setCf;

/**
 * @author Pedro Fialho
 **/
public class HtmlViewVisitorAsync<T> extends HtmlViewVisitorContinuations<T> implements TagsToPrintStream {
    
    private final PrintStream out;

    public HtmlViewVisitorAsync(PrintStream out, boolean isIndented, HtmlContinuation<T> first) {
        super(isIndented, first);
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
        return new HtmlViewVisitorAsync<>(out, isIndented, first.copy(this));
    }
    
    @Override
    public PrintStream out() {
        return this.out;
    }
    
    public CompletableFuture<Void> finishedAsync(T model) {

        TerminationHtmlContinuationNode<T> terminationNode = findLast();

        if (terminationNode.getCf().isDone()) {
            terminationNode = setCf(terminationNode, new CompletableFuture<>());
        }

        this.first.execute(model);

        return terminationNode.getCf();
    }

    private TerminationHtmlContinuationNode<T> findLast() {
        HtmlContinuation<T> node = this.first;

        while (node.next != null){
            node = node.next;
        }

        return (TerminationHtmlContinuationNode<T>) node;
    }
}
