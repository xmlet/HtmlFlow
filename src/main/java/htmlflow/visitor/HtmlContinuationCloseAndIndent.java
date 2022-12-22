package htmlflow.visitor;

/**
 * @author Pedro Fialho
 **/
public class HtmlContinuationCloseAndIndent extends HtmlContinuationSync {
    /**
     * Sets indentation to -1 to inform that visitor should continue with previous indentation.
     * The isClosed is useless here.
     */
    HtmlContinuationCloseAndIndent(HtmlVisitor visitor, HtmlContinuation next) {
        super(-1, false, visitor, next); // The isClosed parameter is useless in this case
    }
    HtmlContinuationCloseAndIndent(HtmlVisitor visitor) {
        super(-1, false, visitor, null); // The isClosed parameter is useless in this case
    }
    
    @Override
    protected final void emitHtml(Object model) {
        /**
         * !!!!! This continuation may follow a dynamic or await block that may create a block or inline element.
         * Block elements increment (depth) indentation at the beginning and decrement at the end.
         * On the other hand, inline elements keep indentation (depth) unmodified.
         * We assume most dynamic or await blocks will create block elements.
         * Thus, here we start by decrementing depth.
         * However, this could not be true for some cases, and we will get inconsistent indentation !!!!
         */
        visitor.depth--;
        visitor.newlineAndIndent();
    }
    
    @Override
    protected HtmlContinuation copy(HtmlVisitor visitor) {
        return new HtmlContinuationCloseAndIndent(
                visitor,
                next != null ? next.copy(visitor) : null); // call copy recursively
    }
}
