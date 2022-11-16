package htmlflow.async;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public final class PublisherOnCompleteHandlerProxy {
    
    private PublisherOnCompleteHandlerProxy(){}
    
    public static <T> PublisherOnCompleteHandler<T> proxyPublisher(Publisher<T> src) {
        return new PublisherOnCompleteHandler<>(src);
    }
    
    public static class PublisherOnCompleteHandler<T> implements Publisher<T> {
        private final Publisher<T> src;
        private OnPublisherCompletion handler;
        
        private PublisherOnCompleteHandler(Publisher<T> s) {
            src = s;
        }
        
        public void addOnCompleteHandler(OnPublisherCompletion action) {
            handler = action;
        }
        
        @Override
        public void subscribe(Subscriber<? super T> downstream) {
            src.subscribe(new Subscriber<T>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    downstream.onSubscribe(subscription);
                }
    
                @Override
                public void onNext(T t) {
                    downstream.onNext(t);
                }
    
                @Override
                public void onError(Throwable throwable) {
                    downstream.onError(throwable);
                }
    
                @Override
                public void onComplete() {
                    handler.onComplete();
                    downstream.onComplete();
                }
            });
        }
    }
}
