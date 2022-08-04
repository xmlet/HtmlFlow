package htmlflow.async.subscribers;

import htmlflow.async.AsyncNode;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.xmlet.htmlapifaster.Element;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ObservableSubscriber<E extends Element, T> implements Observer<T> {
    private final BiConsumer<Supplier<E>, AsyncNode<T>> finalizer;
    private final Supplier<E> elem;
    private final AsyncNode<T> node;
    
    public ObservableSubscriber(BiConsumer<Supplier<E>, AsyncNode<T>> finalizer, Supplier<E> elem, AsyncNode<T> node) {
        this.finalizer = finalizer;
        this.elem = elem;
        this.node = node;
    }
    
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onNext(@NonNull T e) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onError(@NonNull Throwable e) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onComplete() {
        finalizer.accept(elem, node);
    }
}
