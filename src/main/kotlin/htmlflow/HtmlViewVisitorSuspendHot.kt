package htmlflow

import htmlflow.visitor.HtmlVisitorSuspending
import java.util.function.BiConsumer
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer
import org.xmlet.htmlapifaster.async.AwaitConsumer

/**
 * Hot reload visitor that creates a fresh continuation chain on each render without storing static
 * HTML.
 *
 * This visitor is used for suspending operations in a hot reload context.
 */
class HtmlViewVisitorSuspendHot(
    out: Appendable? = null,
    isIndented: Boolean,
    private val continuationSupplier: () -> PreprocessingVisitorSuspend,
) : HtmlVisitorSuspending(out, isIndented) {
    override fun resolve(model: Any?) {
        // no-op
    }

    override fun clone(isIndented: Boolean): HtmlViewVisitorSuspendHot =
        HtmlViewVisitorSuspendHot(out, isIndented, continuationSupplier)

    override fun clone(out: Appendable): HtmlViewVisitorSuspendHot =
        HtmlViewVisitorSuspendHot(out, isIndented, continuationSupplier)

    /**
     * Creates a new continuation chain for each render.
     *
     * Since the visit suspending method signature is not suspend, and we cannot immediately execute
     * the suspending block, we build a continuation chain that is executed after the template is
     * resolved and all the elements are visited.
     *
     * Using continuations also allows us to have well-formed HTML and correct suspending/async
     * behavior.
     */
    override suspend fun executeSuspending(model: Any?) {
        val continuationProcessor = continuationSupplier.invoke()
        continuationProcessor.setAppendable(out)
        continuationProcessor.first.executeSuspending(model)
    }

    override fun <E : Element<*, *>, U> visitDynamic(e: E, biConsumer: BiConsumer<E, U>): Unit =
        throw IllegalStateException(
            "Illegal use of visitDynamic in HtmlViewVisitorSuspendHot. Use continuation processor"
        )

    override fun <M, E : Element<*, *>> visitAwait(
        e: E,
        awaitConsumer: AwaitConsumer<E, M?>,
    ): Unit =
        throw IllegalStateException(
            "Illegal use of visitAwait in HtmlViewVisitorSuspendHot. Use continuation processor"
        )

    override fun <M, E : Element<*, *>> visitSuspending(
        e: E,
        suspendConsumer: SuspendConsumer<E, M?>,
    ): Unit =
        throw IllegalStateException(
            "Illegal use of visitSuspending in HtmlViewVisitorSuspendHot. Use continuation processor"
        )
}
