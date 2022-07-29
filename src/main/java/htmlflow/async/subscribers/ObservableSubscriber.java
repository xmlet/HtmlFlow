package htmlflow.async.subscribers;

import htmlflow.async.AsyncNode;
import org.xmlet.htmlapifaster.Element;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ObservableSubscriber<E extends Element, T> extends AbstractObservableSubscriber<T> {
    private final BiConsumer<Supplier<E>, AsyncNode<T>> finalizer;
    private final Supplier<E> elem;
    private final AsyncNode<T> node;
    
    public ObservableSubscriber(BiConsumer<Supplier<E>, AsyncNode<T>> finalizer, Supplier<E> elem, AsyncNode<T> node) {
        this.finalizer = finalizer;
        this.elem = elem;
        this.node = node;
    }
    
    @Override
    public void onComplete() {
        finalizer.accept(elem, node);
    }
}
