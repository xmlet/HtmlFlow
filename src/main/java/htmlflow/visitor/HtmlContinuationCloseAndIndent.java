package htmlflow.visitor;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationCloseAndIndent<U> extends HtmlContinuation<U> {
    /**
     * Sets indentation to -1 to inform that visitor should continue with previous indentation.
     * The isClosed is useless here.
     */
    HtmlContinuationCloseAndIndent(HtmlVisitor visitor, HtmlContinuation<U> next) {
        super(-1, false, visitor, next); // The isClosed parameter is useless in this case
    }
    HtmlContinuationCloseAndIndent(HtmlVisitor visitor) {
        super(-1, false, visitor, null); // The isClosed parameter is useless in this case
    }
    
    @Override
    public void execute(U model) {
        if (currentDepth >= 0) {
            this.visitor.isClosed = isClosed;
            this.visitor.depth = currentDepth;
        }
        emitHtml(model);
        if (next != null) {
            next.execute(model);
        }
    }
    
    @Override
    protected void emitHtml(U model) {
        //TODO ADD COMMENT
        visitor.depth--;
        visitor.newlineAndIndent();
    }
    
    @Override
    protected HtmlContinuation<U> copy(HtmlVisitor visitor) {
        return new HtmlContinuationCloseAndIndent<>(
                visitor,
                next != null ? next.copy(visitor) : null); // call copy recursively
    }
}
