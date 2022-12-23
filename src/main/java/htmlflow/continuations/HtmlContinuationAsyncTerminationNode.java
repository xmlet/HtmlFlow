package htmlflow.continuations;

import htmlflow.visitor.HtmlVisitor;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationAsyncTerminationNode extends HtmlContinuation {

    private final CompletableFuture<Void> cf;

    public HtmlContinuationAsyncTerminationNode(CompletableFuture<Void> cf) {
        super(-1, false, null, null);
        this.cf = cf;
    }

    public void execute(Object model) {
        cf.complete(null);
    }

    @Override
    public HtmlContinuationAsyncTerminationNode copy(HtmlVisitor visitor) {
        throw new UnsupportedOperationException("Used once and never copied!");
    }
}
