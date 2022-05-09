package htmlflow.async.subscribers;

public class PreviousAsyncObservableSubscriber<T> extends AbstractObservableSubscriber<T> {
    
    private final Runnable onTermination;
    
    public PreviousAsyncObservableSubscriber(Runnable onTermination) {
        this.onTermination = onTermination;
    }
    
    @Override
    public void onComplete() {
        onTermination.run();
    }
}
