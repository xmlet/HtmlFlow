package htmlflow.async.nodes;

import java.util.concurrent.CompletableFuture;

public abstract class ContinuationNode {
    private ContinuationNode next;
    private State state = State.WAITING;
    private CompletableFuture<Void> cf;
    
    protected ContinuationNode(State state) {
        this.state = state;
    }
    
    public void setNext(ContinuationNode next) {
        this.next = next;
    }
    
    public ContinuationNode getNext() {
        return this.next;
    }
    
    public abstract void execute();
    
    public void setRunning() {
        this.state = State.RUNNING;
    }
    
    public boolean isDone() {
        return this.state.isDone();
    }
    
    public boolean isRunning() {
        return this.state.isRunning();
    }
    
    public boolean isWaiting() {
        return this.state.isWaiting();
    }
    
    
    public void setDone() {
        this.state = State.DONE;
        if (this.cf != null) {
            this.cf.complete(null);
        }
    }
    
    public void setCfForCompletion(CompletableFuture<Void> cf) {
        this.cf = cf;
    }
}
