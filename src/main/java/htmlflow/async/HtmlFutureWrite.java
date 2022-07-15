package htmlflow.async;

import htmlflow.DynamicHtml;
import htmlflow.HtmlVisitorAsync;
import htmlflow.HtmlVisitorCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

public class HtmlFutureWrite<V> implements Future<V> {
    
    private final HtmlVisitorAsync visitorCache;
    
    public <T> HtmlFutureWrite(HtmlVisitorAsync visitorCache, BiConsumer<DynamicHtml<T>,T> binder, DynamicHtml<T> view, T model) {
        this.visitorCache = visitorCache;
        binder.accept(view, model);
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        return this.visitorCache.getActions().stream().allMatch(AsyncNode::isDone);
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        while (!this.visitorCache.getActions().stream().allMatch(AsyncNode::isDone));
    
        return null;
    }
    
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return new FutureTask<V>(() -> {
            this.get();
            return null;
        }).get(timeout, unit);
    }
}
