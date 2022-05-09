package htmlflow.async.subscribers;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class AbstractObservableSubscriber<T> implements Observer<T> {
    
    @Override
    public void onSubscribe(@NonNull Disposable d) {}
    
    @Override
    public void onError(@NonNull Throwable e) {}
    
    @Override
    public void onNext(@NonNull T t) {
    
    }
}
