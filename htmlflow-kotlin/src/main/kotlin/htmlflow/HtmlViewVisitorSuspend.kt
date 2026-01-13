package htmlflow

import htmlflow.continuations.HtmlContinuationSuspendable
import htmlflow.visitor.HtmlVisitorSuspending
import java.util.function.BiConsumer
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer
import org.xmlet.htmlapifaster.async.AwaitConsumer

/**
 * Intentionally pass null to out Appendable. Since this visitor allows concurrency, due to its
 * asynchronous nature, then we must clone it on each resolution (i.e. finishAsync()) to avoid
 * sharing continuations across different tasks and set a new out Appendable.
 */
open class HtmlViewVisitorSuspend(
    out: Appendable? = null,
    isIndented: Boolean,
    first: HtmlContinuationSuspendable?,
) : HtmlVisitorSuspending(out, isIndented) {

    private val first = first?.copy(this) as? HtmlContinuationSuspendable

    override fun resolve(model: Any?) {
        first?.execute(model)
    }

    override fun clone(isIndented: Boolean): HtmlViewVisitorSuspend =
        HtmlViewVisitorSuspend(out, isIndented, first)

    override fun clone(out: Appendable): HtmlViewVisitorSuspend =
        HtmlViewVisitorSuspend(out, isIndented, first)

    override suspend fun executeSuspending(model: Any?) {
        first?.executeSuspending(model)
    }

    override fun <E : Element<*, *>?, U : Any?> visitDynamic(
        element: E?,
        consumer: BiConsumer<E?, U?>?,
    ): Unit =
        throw IllegalStateException(
            "Illegal use of visitDynamic in HtmlViewVisitorSuspend. Use preprocessing visitor before creating a HtmlViewSuspend."
        )

    override fun <M : Any?, E : Element<*, *>?> visitAwait(
        element: E?,
        asyncAction: AwaitConsumer<E?, M?>?,
    ): Unit =
        throw IllegalStateException(
            "Illegal use of visitAwait in HtmlViewVisitorSuspend. Use preprocessing visitor before creating a HtmlViewSuspend."
        )
}
