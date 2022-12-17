package htmlflow.visitor;

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationAsync<E extends Element, T> extends HtmlContinuation<T> {
    
    private final E element;
    private final AwaitConsumer<E,T> consumer;

    HtmlContinuationAsync(int currentDepth,
                          boolean isClosed,
                          E element,
                          AwaitConsumer<E,T> consumer,
                          HtmlVisitor visitor,
                          HtmlContinuation<T> next) {
        super(currentDepth, isClosed, visitor, next);
        this.element = element;
        this.consumer = consumer;
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
        this.consumer.accept(element, model, () -> {
            if (next != null) {
                next.execute(model);
            }
        });
    }
    
    
    @Override
    protected HtmlContinuation<T> copy(HtmlVisitor v) {
        return new HtmlContinuationAsync<>(
                currentDepth,
                isClosed,
                copyElement(v),
                consumer,
                v,
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
