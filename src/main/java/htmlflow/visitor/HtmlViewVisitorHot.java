package htmlflow.visitor;

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.function.BiConsumer;

/**
 * Hot reload visitor that processes templates without preprocessing optimization.
 * This visitor recalculates the entire HTML on every rendering, making it suitable
 * for development scenarios where template changes need immediate reflection.
 * 
 * <p><strong>Limitations:</strong>
 * <ul>
 *   <li>Does not support async operations (visitAwait)</li>
 *   <li>Does not support suspending operations (visitSuspending)</li>
 *   <li>Performance is significantly slower than optimized visitor</li>
 * </ul>
 * 
 * @see HtmlViewVisitor for optimized visitor with preprocessing
 */
public class HtmlViewVisitorHot extends HtmlVisitor {

    private Object model;

    public HtmlViewVisitorHot(Appendable out, boolean isIndented) {
        super(out, isIndented);
    }

    @Override
    public void resolve(Object model) {
        this.model = model;
    }

    @Override
    public HtmlVisitor clone(boolean isIndented) {
        return new HtmlViewVisitorHot(out, isIndented);
    }

    @Override
    public <E extends Element, U> void visitDynamic(E e, BiConsumer<E, U> biConsumer) {
        biConsumer.accept(e, (U) model);
    }

    @Override
    public <M, E extends Element> void visitAwait(E e, AwaitConsumer<E, M> awaitConsumer) {
        throw new UnsupportedOperationException("Hot reload visitor does not support async operations. Use HtmlViewVisitorAsync instead.");
    }

    @Override
    public <M, E extends Element> void visitSuspending(E e, SuspendConsumer<E, M> suspendConsumer) {
        throw new UnsupportedOperationException("Hot reload visitor does not support suspending operations. Use appropriate async visitor instead.");
    }
}
