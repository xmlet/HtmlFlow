package htmlflow.async.nodes;


public abstract class ContinuationNode {
    protected ContinuationNode next;

    public ContinuationNode setNext(ContinuationNode next) {
        this.next = next;
        return this.next;
    }
    
    public ContinuationNode getNext() {
        return this.next;
    }

    public abstract void execute();
    
    public void resetNode() {
        this.next = null;
    }
}
