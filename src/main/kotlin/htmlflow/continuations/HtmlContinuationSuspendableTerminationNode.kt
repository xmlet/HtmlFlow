package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor

class HtmlContinuationSuspendableTerminationNode : HtmlContinuation(-1, false, null, null) {

    override suspend fun executeSuspending(model: Any?) {
        // Nothing to do
    }

    override fun execute(model: Any?) {
        throw UnsupportedOperationException("Illegal use of suspending terminal node! Only valid in HtmlViewSuspend.")
    }

    /**
     * Since this Node is used only to signal completion, and we do not use the visitor,
     * then we van reuse it.
     */
    override fun copy(visitor: HtmlVisitor?): HtmlContinuationSuspendableTerminationNode {
        return this
    }
}
