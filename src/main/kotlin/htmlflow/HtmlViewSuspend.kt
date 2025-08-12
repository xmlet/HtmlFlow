package htmlflow

import htmlflow.visitor.HtmlVisitorSuspending
import org.xmlet.htmlapifaster.Html

/**
 * Suspendable views can be bound to a domain object with suspending functions based API.
 *
 *  @param <M> Type of the model rendered with this view.
 *
 * @author Miguel Gamboa
 */
open class HtmlViewSuspend<M>(
    /**
     * Function that consumes an HtmlPage to produce HTML elements.
     */
    private val template: HtmlPage.() -> Unit,
    private val visitor: HtmlVisitorSuspending,
    private var threadSafe: Boolean = true
) : HtmlPage() {
    override fun html(): Html<HtmlPage> {
        visitor.write(HEADER)
        return Html(this)
    }

    override fun setIndented(isIndented: Boolean): HtmlViewSuspend<M>? {
        return viewSuspend<M>(template, isIndented, threadSafe, true)
    }

    override fun getVisitor(): HtmlVisitorSuspending {
        return visitor
    }

    override fun getName(): String {
        return "HtmlViewSuspend"
    }

    override fun threadSafe(): HtmlViewSuspend<M> {
        return HtmlViewSuspend(template, visitor)
    }

    open fun threadUnsafe(): HtmlViewSuspend<M> {
        return HtmlViewSuspend(template, visitor, false)
    }

    fun setPreEncoding(preEncoding: Boolean): HtmlViewSuspend<M> {
        return viewSuspend(template, visitor.isIndented, threadSafe, preEncoding)
    }

    open suspend fun write(out: Appendable, model: M?) {
        if (threadSafe) {
            visitor.clone(out).executeSuspending(model)
        } else {
            visitor.setAppendable(out)
            visitor.executeSuspending(model)
        }
    }

    open suspend fun render(): String = render(null)

    open suspend fun render(model: M?) = StringBuilder().let {
        write(it, model)
        it.toString()
    }

}