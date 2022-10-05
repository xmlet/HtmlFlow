package htmlflow.async.nodes;

import org.xmlet.htmlapifaster.Element;

import java.util.function.Supplier;

public class ThenNode<E extends Element> extends ContinuationNode {
    private final Supplier<E> action;
    
    public ThenNode(Supplier<E> action) {
        this.action = action;
    }
    
    @Override
    public void execute() {
        this.action.get();
        if (this.onCompletionHandler != null) {
            this.onCompletionHandler.run();
        }
    }

}
