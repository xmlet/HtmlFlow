package htmlflow.test.model;

import org.reactivestreams.Publisher;

public class AsyncModel<T,R> {
    
    public final Publisher<T> titles;
    public final Publisher<R> items;
    
    public AsyncModel(Publisher<T> titles, Publisher<R> items) {
        this.titles = titles;
        this.items = items;
    }
}
