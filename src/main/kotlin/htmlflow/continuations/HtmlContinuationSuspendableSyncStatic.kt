package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor

/**
 * HtmlContinuationSuspendable for a static HTML block.
 * Sets indentation to -1 to inform that visitor should continue with previous indentation.
 * The isClosed is useless because it just writes what it is in its staticHtmlBlock.
 * @author Miguel Gamboa
 */
class HtmlContinuationSuspendableSyncStatic(
    private val staticHtmlBlock: String,
    visitor: HtmlVisitor,
    next: HtmlContinuation?
) : HtmlContinuationSuspendableSync(-1, false, visitor, next) { // The isClosed parameter is useless in this case of Static HTML block.

    override fun emitHtml(model: Any?) {
        visitor.write(staticHtmlBlock)
    }

    override fun copy(v: HtmlVisitor): HtmlContinuation? {
        return HtmlContinuationSuspendableSyncStatic(
            staticHtmlBlock,
            v,
            next?.copy(v)
        ) // call copy recursively
    }
}