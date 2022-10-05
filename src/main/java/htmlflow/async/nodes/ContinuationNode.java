package htmlflow.async.nodes;


public abstract class ContinuationNode {
    protected ContinuationNode next;
    protected Runnable onCompletionHandler;
    
    public void setNext(ContinuationNode next) {
        this.next = next;
    }
    
    public ContinuationNode getNext() {
        return this.next;
    }
    
    public abstract void execute();
    
    public void onCompletion(Runnable onCompletionHandler) {
        this.onCompletionHandler = onCompletionHandler;
    }
}
