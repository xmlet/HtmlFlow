package htmlflow.async;

import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncNode<T> implements Cloneable {
    public final Runnable asyncAction;
    public AsyncNode next;
    public ChildNode childNode;
    public final Publisher<T> publisher;
    public final CompletableFuture<Void> cf = new CompletableFuture<>();
    
    private State state = State.WAITING;
    
    public AsyncNode(AsyncNode next, ChildNode childNode, Runnable asyncAction, Publisher<T> publisher) {
        this.next = next;
        this.childNode = childNode;
        this.asyncAction = asyncAction;
        this.publisher = publisher;
    }
    
    public boolean isDone() {
        return cf.isDone() && state == State.DONE;
    }
    
    public boolean isRunning() {
        return state == State.RUNNING;
    }
    
    public boolean isWaiting() {
        return state == State.WAITING;
    }
    
    @Override
    public AsyncNode<T> clone() {
        return new AsyncNode<>(this.next, this.childNode, this.asyncAction, this.publisher);
    }
    
    public void setDone() {
        this.cf.complete(null);
        this.state = State.DONE;
    }
    
    public void setRunning() {
        this.asyncAction.run();
        this.state = State.RUNNING;
    }
    
    public static class ChildNode<E extends Element> {
        public Supplier<E> elem;
        public ChildNode(Supplier<E> elem) {
            this.elem = elem;
        }
        
    }
    
    private enum State {
        WAITING,
        RUNNING,
        DONE
    }
    
}
