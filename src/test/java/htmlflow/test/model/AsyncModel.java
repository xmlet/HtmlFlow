package htmlflow.test.model;

import io.reactivex.rxjava3.core.Observable;

public class AsyncModel<T,R> {
    
    public final Observable<T> titles;
    public final Observable<R> items;
    
    public AsyncModel(Observable<T> titles, Observable<R> items) {
        this.titles = titles;
        this.items = items;
    }
}
