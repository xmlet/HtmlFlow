package htmlflow

import org.xmlet.htmlapifaster.Html

/**
 * Suspendable views can be bound to a domain object with suspending functions based API.
 *
 *  @param <M> Type of the model rendered with this view.
 *
 * @author Miguel Gamboa
 */
class HtmlViewSuspend<M>(
    /**
     * Function that consumes an HtmlPage to produce HTML elements.
     */
    private val template: HtmlPage.() -> Unit,
    private val visitor: HtmlViewVisitorSuspend,
    private var threadSafe: Boolean = true
) : HtmlPage() {
    override fun html(): Html<HtmlPage> {
        visitor.write(HEADER)
        return Html(this)
    }

    override fun setIndented(isIndented: Boolean): HtmlViewSuspend<M>? {
        return viewSuspend<M>(template, isIndented, threadSafe)
    }

    override fun getVisitor(): HtmlViewVisitorSuspend {
        return visitor
    }

    override fun getName(): String {
        return "HtmlViewSuspend"
    }

    override fun threadSafe(): HtmlViewSuspend<M> {
        return HtmlViewSuspend(template, visitor)
    }

    fun threadUnsafe(): HtmlViewSuspend<M> {
        return HtmlViewSuspend(template, visitor, false)
    }


    suspend fun write(out: Appendable, model: M?) {
        if (threadSafe) {
            visitor.clone(out).first.executeSuspending(model)
        } else {
            visitor.setAppendable(out)
            visitor.first.executeSuspending(model)
        }
    }

    suspend fun render(): String = render(null)

    suspend fun render(model: M?) = StringBuilder().let {
        write(it, model)
        it.toString()
    }

}