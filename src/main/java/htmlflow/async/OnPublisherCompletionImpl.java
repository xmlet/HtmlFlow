package htmlflow.async;

import org.xmlet.htmlapifaster.async.OnPublisherCompletion;

public class OnPublisherCompletionImpl<T> implements OnPublisherCompletion {
    
    private final AsyncNode<T> node;
    private final OnNodeAdvance onNodeAdvance;
    
    public OnPublisherCompletionImpl(AsyncNode<T> node, OnNodeAdvance onNodeAdvance) {
        this.node = node;
        this.onNodeAdvance = onNodeAdvance;
    }
    
    @Override
    public void onComplete() {
        node.setDone();
    
        final AsyncNode.ChildNode childNode = node.childNode;
        if (childNode != null) {
            childNode.elem.get();
        }
        
        this.onNodeAdvance.advance();
    }
    
    @FunctionalInterface
    public interface OnNodeAdvance {
        void advance();
    }
}
