package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationAsync;
import htmlflow.continuations.HtmlContinuationSyncCloseAndIndent;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;


/**
 * @author Pedro Fialho
 **/
public class PreprocessingVisitorAsync extends PreprocessingVisitor {

    public PreprocessingVisitorAsync(boolean isIndented) {
        super(isIndented);
    }

    @Override
    public <M, E extends Element> void visitAwait(E element, AwaitConsumer<E, M> asyncHtmlBlock) {
        /**
         * Creates an HtmlContinuation for the async block.
         */
        HtmlContinuation asyncCont = new HtmlContinuationAsync<>(
                depth,
                isClosed,
                element,
                asyncHtmlBlock,
                this,
                new HtmlContinuationSyncCloseAndIndent(this));
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the asyncCont.
         */
        chainContinuationStatic(asyncCont);
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        indentAndAdvanceStaticBlockIndex();
    }
}
