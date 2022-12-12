package htmlflow.async;

import htmlflow.visitor.HtmlContinuation;
import htmlflow.visitor.HtmlVisitor;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pedro Fialho
 **/
public class TerminationHtmlContinuationNode<T> extends HtmlContinuation<T> {

    private final CompletableFuture<Void> cf = new CompletableFuture<>();

    public TerminationHtmlContinuationNode(HtmlVisitor visitor) {
        super(-1, false, visitor, null);
    }

    public void execute(T model) {
        this.emitHtml(model);
    }

    @Override
    protected void emitHtml(T model) {
        cf.complete(null);
    }

    public CompletableFuture<Void> getCf() {
        return cf;
    }

    @Override
    protected TerminationHtmlContinuationNode<T> copy(HtmlVisitor visitor) {
        return new TerminationHtmlContinuationNode<>(
                visitor);
    }
}
