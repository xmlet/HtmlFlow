package htmlflow.continuations;

import htmlflow.visitor.HtmlVisitor;

public abstract class HtmlContinuationSync extends HtmlContinuation{
    /**
     * @param currentDepth Indentation depth associated to this block.
     * @param isClosed
     * @param visitor
     * @param next
     */
    protected HtmlContinuationSync(int currentDepth, boolean isClosed, HtmlVisitor visitor, HtmlContinuation next) {
        super(currentDepth, isClosed, visitor, next);
    }
    /**
     * Executes this continuation and calls the next one if exist.
     *
     * @param model
     */
    @Override
    public final void execute(Object model) {
        if (currentDepth >= 0) {
            this.visitor.setIsClosed(isClosed);
            this.visitor.setDepth(currentDepth);
        }
        emitHtml(model);
        if (next != null) {
            next.execute(model);
        }
    }
    /**
     * Hook method to emit HTML.
     *
     * @param model
     */
    protected abstract void emitHtml(Object model);
}
