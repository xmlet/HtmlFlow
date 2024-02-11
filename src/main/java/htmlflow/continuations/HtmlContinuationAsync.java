package htmlflow.continuations;

import htmlflow.visitor.HtmlVisitor;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

/**
 * HtmlContinuation for an asynchronous block (i.e. AwaitConsumer) depending of an asynchronous object model.
 * The next continuation will be invoked on completion of asynchronous object model.
 *
 * @param <E> the type of the parent HTML element received by the dynamic HTML block.
 * @param <T> the type of the template's model.
 */
public class HtmlContinuationAsync<E extends Element, T> extends HtmlContinuation {
    
    final E element;
    final AwaitConsumer<E,T> consumer;

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
                copyElement(element, v),
                consumer,
                v,
                next != null ? next.copy(v) : null); // call copy recursively
    }
}
