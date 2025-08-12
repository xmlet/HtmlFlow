package htmlflow;

import htmlflow.continuations.HtmlContinuationSuspendable
import htmlflow.visitor.HtmlViewVisitor

/**
 * Intentionally pass null to out Appendable.
 * Since this visitor allows concurrency, due to its asynchronous nature, then we must
 * clone it on each resolution (i.e. finishAsync()) to avoid sharing continuations across
 * different tasks and set a new out Appendable.
 */
class HtmlViewVisitorSuspend(
    out: Appendable? = null,
    isIndented: Boolean,
    first: HtmlContinuationSuspendable?
) : HtmlViewVisitor(out, isIndented, first) {

    override fun getFirst(): HtmlContinuationSuspendable? {
        return first as? HtmlContinuationSuspendable
    }

    /**
     * The last node to be processed.
     */
    private var last: HtmlContinuationSuspendable? = null

    init {
        last = findLast()
    }

    override fun clone(isIndented: Boolean): HtmlViewVisitorSuspend {
        return HtmlViewVisitorSuspend(out, isIndented, getFirst())
    }

    fun clone(out: Appendable): HtmlViewVisitorSuspend {
        return HtmlViewVisitorSuspend(out, isIndented, getFirst())
    }

    fun findLast(): HtmlContinuationSuspendable? {
        var node = first
        while (node.next != null) {
            node = node.next
        }
        return node as? HtmlContinuationSuspendable
    }

}
