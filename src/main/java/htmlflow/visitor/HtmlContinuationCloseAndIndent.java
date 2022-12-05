package htmlflow.visitor;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationCloseAndIndent<U> extends HtmlContinuation<U> {
    
    HtmlContinuationCloseAndIndent(int currentDepth, boolean isClosed, HtmlVisitor visitor, HtmlContinuation<U> next) {
        super(currentDepth, isClosed, visitor, next);
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
        visitor.newlineAndIndent();
    }
    
    @Override
    protected HtmlContinuation<U> copy(HtmlVisitor visitor) {
        return null;
    }
}
