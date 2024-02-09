package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor

class HtmlContinuationSuspendableTerminationNode : HtmlContinuation(-1, false, null, null) {

    override suspend fun executeSuspending(model: Any?) {
        // Nothing to do
    }

    override fun execute(model: Any?) {
        throw UnsupportedOperationException("Illegal use of suspending terminal node! Only valid in HtmlViewSuspend.")
    }


    override fun copy(visitor: HtmlVisitor?): HtmlContinuationAsyncTerminationNode? {
        throw UnsupportedOperationException("Used once and never copied!")
    }
}
