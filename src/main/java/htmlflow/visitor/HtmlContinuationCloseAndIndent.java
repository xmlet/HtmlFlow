package htmlflow.visitor;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationCloseAndIndent<T> extends HtmlContinuation<T> {
    /**
     * Sets indentation to -1 to inform that visitor should continue with previous indentation.
     * The isClosed is useless here.
     */
    HtmlContinuationCloseAndIndent(HtmlVisitor visitor, HtmlContinuation<T> next) {
        super(-1, false, visitor, next); // The isClosed parameter is useless in this case
    }
    HtmlContinuationCloseAndIndent(HtmlVisitor visitor) {
        super(-1, false, visitor, null); // The isClosed parameter is useless in this case
    }
    
    @Override
    public void execute(T model) {
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
    protected void emitHtml(T model) {
        /*
         * !!!!
         * This is needed to adjust the depth when coming out of a await/dynamic block.
         * With the usage of the Trim the first element is being left out of the next static continuation
         * So it's now expected that the user puts `.__()` right after the await/dynamic block.
         * This `visitor.depth--` is to replicate that call.
         * !!!!
         */
        visitor.depth--;
        visitor.newlineAndIndent();
    }
    
    @Override
    protected HtmlContinuation<T> copy(HtmlVisitor visitor) {
        return new HtmlContinuationCloseAndIndent<>(
                visitor,
                next != null ? next.copy(visitor) : null); // call copy recursively
    }
}
