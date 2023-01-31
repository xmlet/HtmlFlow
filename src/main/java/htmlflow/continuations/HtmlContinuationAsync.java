package htmlflow.continuations;

import htmlflow.visitor.HtmlVisitor;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * HtmlContinuation for an asynchronous block (i.e. AwaitConsumer) depending of an asynchronous object model.
 * The next continuation will be invoked on completion of asynchronous object model.
 *
 * @param <E> the type of the parent HTML element received by the dynamic HTML block.
 * @param <T> the type of the template's model.
 */
public class HtmlContinuationAsync<E extends Element, T> extends HtmlContinuation {
    
    private final E element;
    private final AwaitConsumer<E,T> consumer;

    public HtmlContinuationAsync(int currentDepth,
                                 boolean isClosed,
                                 E element,
                                 AwaitConsumer<E,T> consumer,
                                 HtmlVisitor visitor,
                                 HtmlContinuation next) {
        super(currentDepth, isClosed, visitor, next);
        this.element = element;
        this.consumer = consumer;
    }
    
    @Override
    public final void execute(Object model) {
        if (currentDepth >= 0) {
            this.visitor.setIsClosed(isClosed);
            this.visitor.setDepth(currentDepth);
        }
        this.consumer.accept(element, (T) model, () -> {
            if (next != null) {
                next.execute(model);
            }
        });
    }
    
    @Override
    public HtmlContinuation copy(HtmlVisitor v) {
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
