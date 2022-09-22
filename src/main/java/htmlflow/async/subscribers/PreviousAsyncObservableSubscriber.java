package htmlflow.async.subscribers;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PreviousAsyncObservableSubscriber<T> implements Subscriber<T> {
    
    private final Runnable onTermination;
    
    public PreviousAsyncObservableSubscriber(Runnable onTermination) {
        this.onTermination = onTermination;
    }
    
    @Override
    public void onSubscribe(Subscription subscription) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onError(Throwable e) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onComplete() {
        onTermination.run();
    }
}
