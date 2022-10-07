package htmlflow.async.nodes;

public class AsyncNode extends ContinuationNode {
    public final Runnable asyncAction;

    public AsyncNode(Runnable asyncAction) {
        this.asyncAction = asyncAction;
    }
    
    @Override
    public void execute() {
        this.asyncAction.run();
    }

    /**
     * If there is a next ContinuationNode then execute it.
     * This handler is registered onCompletion of wrapper Publisher from HtmlApiFaster.
     */
    public final void executeNextNode() {
        final ContinuationNode next = this.next;
        if (next != null) {
            next.execute();
        }
    }
}
