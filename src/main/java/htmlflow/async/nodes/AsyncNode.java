package htmlflow.async.nodes;

import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncNode<T> extends ContinuationNode {
    public final Runnable asyncAction;
    public final Publisher<T> publisher;
    
    public AsyncNode(Runnable asyncAction, Publisher<T> publisher) {
        super(State.WAITING);
        this.asyncAction = asyncAction;
        this.publisher = publisher;
    }
    
    @Override
    public AsyncNode<T> clone() {
        return new AsyncNode<>(this.asyncAction, this.publisher);
    }
    
    @Override
    public void execute() {
        this.asyncAction.run();
        this.setRunning();
    }
    
}
