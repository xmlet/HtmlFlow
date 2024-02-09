package htmlflow;

import htmlflow.continuations.HtmlContinuation
import htmlflow.continuations.HtmlContinuationSuspendableTerminationNode
import htmlflow.visitor.HtmlViewVisitor
import htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter

/**
 * Intentionally pass null to out Appendable.
 * Since this visitor allows concurrency, due to its asynchronous nature, then we must
 * clone it on each resolution (i.e. finishAsync()) to avoid sharing continuations across
 * different tasks and set a new out Appendable.
 */
class HtmlViewVisitorSuspend(
    out: Appendable? = null,
    isIndented: Boolean,
    first: HtmlContinuation?
) : HtmlViewVisitor (
    out, isIndented, first
) {
    /**
     * The last node to be processed.
     */
    private var last: HtmlContinuation? = null

    init {
        last = findLast()
    }

    override fun clone(isIndented: Boolean): HtmlViewVisitorSuspend {
        return HtmlViewVisitorSuspend(out, isIndented, first)
    }

    fun clone(out: Appendable): HtmlViewVisitorSuspend {
        return HtmlViewVisitorSuspend(out, isIndented, first)
    }

    suspend fun finished(model: Any?) {
        /**
         * !!!!! Enhance => We are not instantiating new CompletableFuture and can reuse the TerminationNode
         */
        val terminationNode = HtmlContinuationSuspendableTerminationNode()
        /**
         * Chain terminationNode next to the last node.
         * Keep last pointing to the same node to replace the terminationNode on
         * others render async.
         */
        HtmlContinuationSetter.setNext(last, terminationNode)
        /**
         * Initializes render on first node.
         */
        first.executeSuspending(model)
    }

    private fun findLast(): HtmlContinuation? {
        var node = first
        while (node.next != null) {
            node = node.next
        }
        return node
    }

}
