package htmlflow.async.subscribers;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PreviousAsyncObservableSubscriber<T> implements Observer<T> {
    
    private final Runnable onTermination;
    
    public PreviousAsyncObservableSubscriber(Runnable onTermination) {
        this.onTermination = onTermination;
    }
    
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        // Not used because we are not subscribing to this particular Subscriber
        // We are just using this to subscribe to an Observable
    }
    
    @Override
    public void onNext(@NonNull T t) {
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
        onTermination.run();
    }
}
