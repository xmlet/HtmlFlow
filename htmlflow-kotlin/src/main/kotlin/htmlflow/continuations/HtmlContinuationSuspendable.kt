package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class HtmlContinuationSuspendable(
    currentDepth: Int,
    isClosed: Boolean,
    visitor: HtmlVisitor?,
    next: HtmlContinuation?,
) : HtmlContinuation(currentDepth, isClosed, visitor, next) {
    protected abstract val nextSuspendable: HtmlContinuationSuspendable?

    open suspend fun executeSuspending(model: Any?) {
        throw UnsupportedOperationException("Illegal use of executeSuspending!")
    }
}
