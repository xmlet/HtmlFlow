package htmlflow.async;

import htmlflow.visitor.HtmlContinuation;
import htmlflow.visitor.HtmlVisitor;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pedro Fialho
 **/
public class TerminationHtmlContinuationNode extends HtmlContinuation {

    private final CompletableFuture<Void> cf;

    public TerminationHtmlContinuationNode(CompletableFuture<Void> cf) {
        super(-1, false, null, null);
        this.cf = cf;
    }

    public void execute(Object model) {
        cf.complete(null);
    }

    @Override
    protected TerminationHtmlContinuationNode copy(HtmlVisitor visitor) {
        throw new UnsupportedOperationException("Used once and never copied!");
    }
}
