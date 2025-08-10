package htmlflow

import htmlflow.continuations.HtmlContinuation
import htmlflow.visitor.HtmlVisitorSuspending
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer
import org.xmlet.htmlapifaster.async.AwaitConsumer
import java.util.function.BiConsumer

/**
 * Intentionally pass null to out Appendable.
 * Since this visitor allows concurrency, due to its asynchronous nature, then we must
 * clone it on each resolution (i.e. finishAsync()) to avoid sharing continuations across
 * different tasks and set a new out Appendable.
 */
open class HtmlViewVisitorSuspend(
    out: Appendable? = null,
    isIndented: Boolean,
    first: HtmlContinuation
) : HtmlVisitorSuspending (
    out, isIndented
) {
    private val first: HtmlContinuation = first.copy(this)

    override fun resolve(model: Any?) {
        first.execute(model)
    }

    override fun clone(isIndented: Boolean): HtmlViewVisitorSuspend {
        return HtmlViewVisitorSuspend(out, isIndented, first)
    }

    override fun clone(out: Appendable): HtmlViewVisitorSuspend {
        return HtmlViewVisitorSuspend(out, isIndented, first)
    }

    override suspend fun executeSuspending(model: Any?) {
        first.executeSuspending(model)
    }

    override fun <E : Element<*, *>?, U : Any?> visitDynamic(
        element: E?,
        consumer: BiConsumer<E?, U?>?
    ) {
        throw IllegalStateException("Illegal use of visitDynamic in HtmlViewVisitorSuspend. Use preprocessing visitor before creating a HtmlViewSuspend.")
    }

    override fun <M : Any?, E : Element<*, *>?> visitAwait(
        element: E?,
        asyncAction: AwaitConsumer<E?, M?>?
    ) {
        throw IllegalStateException("Illegal use of visitAwait in HtmlViewVisitorSuspend. Use preprocessing visitor before creating a HtmlViewSuspend.")
    }

    override fun <M : Any?, E : Element<*, *>?> visitSuspending(
        element: E?,
        suspendAction: SuspendConsumer<E?, M?>?
    ) {
        throw IllegalStateException("Illegal use of visitSuspending in HtmlViewVisitorSuspend. Use preprocessing visitor before creating a HtmlViewSuspend.")
    }

}
