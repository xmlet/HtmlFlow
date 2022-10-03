package htmlflow.async.nodes;

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.OnPublisherCompletion;

import java.util.function.Supplier;

public class ThenNode<E extends Element> extends ContinuationNode {
    private final Supplier<E> action;
    private final OnPublisherCompletion onPublisherCompletion;
    
    public ThenNode(Supplier<E> action, OnPublisherCompletion onPublisherCompletion) {
        super(State.WAITING);
        this.action = action;
        this.onPublisherCompletion = onPublisherCompletion;
    }
    
    @Override
    public void execute() {
        this.setRunning();
        this.action.get();
        this.setDone();
        this.onPublisherCompletion.onComplete();
    }
}
