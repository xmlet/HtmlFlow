package htmlflow.visitor;

import htmlflow.async.PublisherOnCompleteHandlerProxy;
import htmlflow.async.PublisherOnCompleteHandlerProxy.PublisherOnCompleteHandler;
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static htmlflow.async.PublisherOnCompleteHandlerProxy.proxyPublisher;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationAsync<E extends Element, U, T> extends HtmlContinuation<T> {
    
    private final E element;
    private final BiConsumer<E, Publisher<U>> consumer;
    private final Function<T,Publisher<U>> modelToObs;
    
    HtmlContinuationAsync(int currentDepth,
                          boolean isClosed,
                          E element,
                          BiConsumer<E, Publisher<U>> consumer,
                          HtmlVisitor visitor,
                          Function<T,Publisher<U>> modelToObs,
                          HtmlContinuation<T> next) {
        super(currentDepth, isClosed, visitor, next);
        this.element = element;
        this.consumer = consumer;
        this.modelToObs = modelToObs;
    }
    
    @Override
    public void execute(T model) {
        if (currentDepth >= 0) {
            this.visitor.isClosed = isClosed;
            this.visitor.depth = currentDepth;
        }
        
        emitHtml(model);
    }
    
    @Override
    protected void emitHtml(T model) {
        Publisher<U> source = modelToObs.apply(model);
    
        final PublisherOnCompleteHandlerProxy.PublisherOnCompleteHandler<U> proxy = proxyPublisher(source);
        proxy.addOnCompleteHandler(() -> {
            if (this.next != null) {
                this.next.execute(model);
            }
        });
        
        this.consumer.accept(element, proxy);
    }
    
    
    @Override
    protected HtmlContinuation<T> copy(HtmlVisitor v) {
        return new HtmlContinuationAsync<>(
                currentDepth,
                isClosed,
                copyElement(v),
                consumer,
                v,
                modelToObs,
                next != null ? next.copy(v) : null); // call copy recursively
    }
    
    @SuppressWarnings("squid:S3011")
    public E copyElement(HtmlVisitor v) {
        try {
            Constructor<E> ctor = ((Class<E>) element
                    .getClass())
                    .getDeclaredConstructor(Element.class, ElementVisitor.class, boolean.class);
            ctor.setAccessible(true);
            return ctor.newInstance(element.getParent(), v, false); // false to not dispatch visit now
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
