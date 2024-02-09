package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor

/**
 * Sets indentation to -1 to inform that visitor should continue with previous indentation.
 * The isClosed is useless here.
 *
 * @author Miguel Gamboa
 */
class HtmlContinuationSuspendableSyncCloseAndIndent(
    visitor: HtmlVisitor,
    next: HtmlContinuation? = null
) : HtmlContinuationSuspendableSync(-1, false, visitor, next) {

    override fun emitHtml(model: Any?) {
        /**
         * !!!!! This continuation may follow a dynamic or await block that may create a block or inline element.
         * Block elements increment (depth) indentation at the beginning and decrement at the end.
         * On the other hand, inline elements keep indentation (depth) unmodified.
         * We assume most dynamic or await blocks will create block elements.
         * Thus, here we start by decrementing depth.
         * However, this could not be true for some cases, and we will get inconsistent indentation !!!!
         */
        visitor.depth = visitor.depth - 1
        visitor.newlineAndIndent()

    }

    override fun copy(visitor: HtmlVisitor): HtmlContinuation? {
        return HtmlContinuationSuspendableSyncCloseAndIndent(
            visitor,
            next?.copy(visitor)
        ) // call copy recursively
    }
}