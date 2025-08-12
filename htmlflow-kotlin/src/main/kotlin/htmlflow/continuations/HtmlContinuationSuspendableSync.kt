package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor

/**
 * @author Miguel Gamboa
 */
abstract class HtmlContinuationSuspendableSync(
    currentDepth: Int,
    isClosed: Boolean,
    visitor: HtmlVisitor,
    next: HtmlContinuation?
) : HtmlContinuationSuspendable(currentDepth, isClosed, visitor, next) {
    override val nextSuspendable: HtmlContinuationSuspendable? = next as? HtmlContinuationSuspendable
    /**
     * Executes this continuation and calls the next one if exist.
     *
     * @param model
     */
    override fun execute(model: Any?) {
        throw UnsupportedOperationException("Illegal use of execute in suspendable continuation! Should use executeSuspending.")
    }

    final override suspend fun executeSuspending(model: Any?) {
        if (currentDepth >= 0) {
            visitor.setIsClosed(isClosed)
            visitor.depth = currentDepth
        }
        emitHtml(model)
        nextSuspendable?.executeSuspending(model)
    }

    /**
     * Hook method to emit HTML.
     *
     * @param model
     */
    protected abstract fun emitHtml(model: Any?)
}