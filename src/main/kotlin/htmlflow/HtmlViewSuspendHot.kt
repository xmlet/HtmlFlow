package htmlflow

import htmlflow.visitor.HtmlVisitorSuspending

/**
 * Suspendable hot reload views can be bound to a domain object with suspending functions based API.
 * Dynamic views are unoptimized and do not store static HTML blocks, recalculating HTML on every rendering.
 * This makes them suitable for development scenarios where template changes need immediate reflection.
 *
 * They are not recommended for production use due to performance overhead.
 *
 *  @param <M> Type of the model rendered with this view.
 *
 * @author Bernardo Pereira
 */
class HtmlViewSuspendHot<M>(
    /**
     * Function that consumes an HtmlPage to produce HTML elements.
     */
    private val template: HtmlPage.() -> Unit,
    private val visitor: HtmlVisitorSuspending,
    private var threadSafe: Boolean = true
) : HtmlViewSuspend<M>(template, visitor, threadSafe) {

    override fun setIndented(isIndented: Boolean): HtmlViewSuspendHot<M> {
        return HtmlViewSuspendHot(template, visitor, threadSafe)
    }

    override fun getVisitor(): HtmlVisitorSuspending {
        return visitor
    }

    override fun getName(): String {
        return "HtmlViewSuspendHot"
    }

    override fun threadSafe(): HtmlViewSuspend<M> {
        return HtmlViewSuspendHot(template, visitor, true)
    }

    override fun threadUnsafe(): HtmlViewSuspend<M> {
        return HtmlViewSuspendHot(template, visitor, false)
    }

    override suspend fun write(out: Appendable, model: M?) {
        val currentVisitor = if (threadSafe) {
            visitor.clone(out)
        } else {
            visitor.setAppendable(out)
            visitor
        }
        currentVisitor.executeSuspending(model)
    }
}
