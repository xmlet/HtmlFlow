package htmlflow.visitor;

import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationAsync<E extends Element, U, T> extends HtmlContinuation<T> {
    
    private final E element;
    private final BiConsumer<E, Publisher<U>> consumer;
    private final Publisher<U> source;
    
    HtmlContinuationAsync(int currentDepth,
                          boolean isClosed,
                          E element, BiConsumer<E, Publisher<U>> consumer,
                          HtmlVisitor visitor,
                          Publisher<U> source,
                          HtmlContinuation<T> next) {
        super(currentDepth, isClosed, visitor, next);
        this.element = element;
        this.consumer = consumer;
        this.source = source;
    }
    
    @Override
    protected void emitHtml(T model) {
        this.consumer.accept(element, source);
    }
    
    @Override
    protected HtmlContinuation<T> copy(HtmlVisitor v) {
        return new HtmlContinuationAsync<>(
                currentDepth,
                isClosed,
                copyElement(v),
                consumer,
                v,
                this.source,
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
