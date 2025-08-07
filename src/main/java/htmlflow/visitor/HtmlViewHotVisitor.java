package htmlflow.visitor;

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.function.BiConsumer;

public class HtmlViewHotVisitor extends HtmlVisitor {

    private Object model;

    public HtmlViewHotVisitor(Appendable out, boolean isIndented) {
        super(out, isIndented);
    }

    @Override
    public void resolve(Object model) {
        this.model = model;
    }

    @Override
    public HtmlVisitor clone(boolean isIndented) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Element, U> void visitDynamic(E e, BiConsumer<E, U> biConsumer) {
        biConsumer.accept(e, (U) model);
    }

    @Override
    public <M, E extends Element> void visitAwait(E e, AwaitConsumer<E, M> awaitConsumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <M, E extends Element> void visitSuspending(E e, SuspendConsumer<E, M> suspendConsumer) {
        throw new UnsupportedOperationException();
    }
}
