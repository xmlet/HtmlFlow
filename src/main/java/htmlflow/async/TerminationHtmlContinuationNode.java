package htmlflow.async;

import htmlflow.visitor.HtmlContinuation;
import htmlflow.visitor.HtmlVisitor;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pedro Fialho
 **/
public class TerminationHtmlContinuationNode<T> extends HtmlContinuation<T> {

    private final CompletableFuture<Void> cf;

    public TerminationHtmlContinuationNode(CompletableFuture<Void> cf) {
        super(-1, false, null, null);
        this.cf = cf;
    }

    @Override
    public void execute(T model) {
        cf.complete(null);
    }

    @Override
    protected void emitHtml(T model) { /* nothing to emit */}

    @Override
    protected TerminationHtmlContinuationNode<T> copy(HtmlVisitor visitor) {
        throw new UnsupportedOperationException("Used once and never copied!");
    }
}
