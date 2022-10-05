package htmlflow.async.nodes;

import org.reactivestreams.Publisher;

public class AsyncNode<T> extends ContinuationNode {
    public final Runnable asyncAction;
    public final Publisher<T> publisher;
    
    
    public AsyncNode(Runnable asyncAction, Publisher<T> publisher) {
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
    }
}
