package htmlflow.async.nodes;

public enum State {
    WAITING,
    RUNNING,
    DONE;
    
    public boolean isDone() {
        return this == DONE;
    }
    
    public boolean isRunning() {
        return this == RUNNING;
    }
    
    public boolean isWaiting() {
        return this == WAITING;
    }
}
