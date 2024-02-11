package htmlflow

import htmlflow.continuations.*
import htmlflow.visitor.PreprocessingVisitorAsync
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer
import org.xmlet.htmlapifaster.async.AwaitConsumer
import java.util.function.BiConsumer

class PreprocessingVisitorSuspend(isIndented: Boolean) : PreprocessingVisitorAsync(isIndented) {

    /**
     * Here we are creating 2 HtmlContinuation objects: one for previous static HTML and a next one
     * corresponding to the consumer passed to dynamic().
     * We will first create the dynamic continuation that will be the next node of the static continuation.
     *
     * U is the type of the model passed to the dynamic HTML block that is the same as T in this visitor.
     * Yet, since it came from HtmlApiFaster that is not typed by the Model, then we have to use
     * another generic argument for the type of the model.
     *
     * @param element The parent element.
     * @param dynamicHtmlBlock The continuation that consumes the element and a model.
     * @param <E> Type of the parent Element.
     * @param <U> Type of the model passed to the dynamic HTML block that is the same as T in this visitor.
    </U></E> */
    override fun <E : Element<*, *>, U> visitDynamic(element: E, dynamicHtmlBlock: BiConsumer<E, U>) {
        /**
         * Creates an HtmlContinuation for the dynamic block.
         */
        val dynamicCont: HtmlContinuation = HtmlContinuationSuspendableSyncDynamic(
            depth,
            isClosed,
            element,
            dynamicHtmlBlock,
            this,
            HtmlContinuationSuspendableSyncCloseAndIndent(this)
        )
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the dynamicCont.
         */
        chainContinuationSuspendableStatic(dynamicCont)
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        indentAndAdvanceStaticBlockIndex()
    }


    override fun <M, E : Element<*, *>> visitAwait(element: E, asyncHtmlBlock: AwaitConsumer<E, M?>) {
        /**
         * Creates an HtmlContinuation for a suspending block.
         */
        val asyncCont: HtmlContinuation = HtmlContinuationSuspendableAsync(
            depth,
            isClosed,
            element,
            asyncHtmlBlock,
            this,
            HtmlContinuationSuspendableSyncCloseAndIndent(this)
        )
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the suspCont.
         */
        chainContinuationSuspendableStatic(asyncCont)
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        indentAndAdvanceStaticBlockIndex()
    }


    override fun <M, E : Element<*, *>> visitSuspending(element: E, suspendAction: SuspendConsumer<E, M?>) {
        /**
         * Creates an HtmlContinuation for a suspending block.
         */
        val suspCont: HtmlContinuation = HtmlContinuationSuspending(
            depth,
            isClosed,
            element,
            suspendAction,
            this,
            HtmlContinuationSuspendableSyncCloseAndIndent(this)
        )
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the suspCont.
         */
        chainContinuationSuspendableStatic(suspCont)
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        indentAndAdvanceStaticBlockIndex()
    }

    /**
     * Creates the last static HTML block.
     */
    override fun resolve(model: Any?) {
        val staticHtml = sb().substring(staticBlockIndex)
        val staticCont: HtmlContinuation = HtmlContinuationSuspendableSyncStatic(staticHtml.trim { it <= ' ' }, this, null)
        last = if (first == null) staticCont.also {
            first = it // assign both first and last
        } else HtmlContinuationSetter.setNext(
            last,
            staticCont
        ) // append new staticCont and return it to be the new last continuation.
    }

    private fun chainContinuationSuspendableStatic(nextContinuation: HtmlContinuation) {
        val staticHtml = sb().substring(staticBlockIndex)
        val staticHtmlTrimmed = staticHtml.trim { it <= ' ' } // trim to remove the indentation from static block
        val staticCont: HtmlContinuation = HtmlContinuationSuspendableSyncStatic(staticHtmlTrimmed, this, nextContinuation)
        if (first == null) first = staticCont // on first visit initializes the first pointer
        else HtmlContinuationSetter.setNext(last, staticCont) // else append the staticCont to existing chain
        last = nextContinuation.next // advance last to point to the new HtmlContinuationCloseAndIndent
    }

}
