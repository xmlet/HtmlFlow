package htmlflow.async;

import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Element;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncNode<T> implements Cloneable{
    public final Runnable asyncAction;
    public AsyncNode next;
    public ChildNode childNode;
    public final Observable<T> observable;
    public CompletableFuture<Void> cf;
    
    public AsyncNode(AsyncNode next, ChildNode childNode, Runnable asyncAction, Observable<T> observable) {
        this.next = next;
        this.childNode = childNode;
        this.asyncAction = asyncAction;
        this.observable = observable;
    }
    
    public boolean isDone() {
        return cf != null && cf.isDone();
    }
    
    public boolean isRunning() {
        return cf != null;
    }

    public boolean isWaiting() {
        return cf == null;
    }
    
    @Override
    public AsyncNode<T> clone() {
        final AsyncNode<T> node = new AsyncNode<>(this.next, this.childNode, this.asyncAction, this.observable);
        node.cf = this.cf;
        return node;
    }

    public void setDone() {
        this.cf.complete(null);
    }

    public void setRunning() {
        this.cf = new CompletableFuture<>();
    }

    public static class ChildNode<E extends Element> {
        public Supplier<E> elem;
        public OnAsyncAction onAsyncAction;
        
        public ChildNode(Supplier<E> elem, OnAsyncAction onAsyncAction) {
            this.elem = elem;
            this.onAsyncAction = onAsyncAction;
        }
        
    }
    
    public interface OnAsyncAction {
        void trigger(AsyncNode node);
    }

}
