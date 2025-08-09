package htmlflow

import htmlflow.continuations.HtmlContinuation
import htmlflow.continuations.HtmlContinuationSuspendableTerminationNode
import htmlflow.visitor.HtmlVisitorSuspending
import htmlflow.visitor.PreprocessingVisitor
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer
import org.xmlet.htmlapifaster.async.AwaitConsumer
import java.util.function.BiConsumer

/**
 * Hot reload visitor that creates a fresh continuation chain on each render without storing static HTML.
 *
 * This visitor is used for suspending operations in a hot reload context.
 */
class HtmlViewVisitorSuspendHot(
    out: Appendable? = null,
    isIndented: Boolean,
    var template: HtmlPage.() -> Unit
) : HtmlVisitorSuspending(out, isIndented) {

    private var continuationProcessor: PreprocessingVisitorSuspend? = null

    /**
     * Creates a new continuation chain for each render.
     *
     * Since the visit suspending method signature is not suspend, and we
     * cannot immediately execute the suspending block, we build a continuation chain
     * that is executed after the template is resolved and all the elements are visited.
     *
     * This allows us to handle suspending operations in a hot reload context
     * without storing static HTML blocks.
     */
    override fun resolve(model: Any?) {
        continuationProcessor = PreprocessingVisitorSuspend(isIndented)
        val preView = HtmlView<Any>({ continuationProcessor!! }, template, false)
        preView.template.resolve(preView)
        preView.visitor.resolve(null)
        val terminationNode = HtmlContinuationSuspendableTerminationNode()
        PreprocessingVisitor.HtmlContinuationSetter.setNext(findLast(), terminationNode)
    }

    override fun clone(isIndented: Boolean): HtmlViewVisitorSuspendHot {
        return HtmlViewVisitorSuspendHot(out, isIndented, template)
    }

    override fun clone(out: Appendable): HtmlViewVisitorSuspendHot {
        return HtmlViewVisitorSuspendHot(out, isIndented, template)
    }

    override suspend fun executeSuspending(model: Any?) {
        continuationProcessor!!.setAppendable(out)
        continuationProcessor!!.first.executeSuspending(model)
    }

    override fun <E : Element<*, *>, U> visitDynamic(e: E, biConsumer: BiConsumer<E, U>) {
        continuationProcessor?.visitDynamic(e, biConsumer)
    }

    override fun <M, E : Element<*, *>> visitAwait(e: E, awaitConsumer: AwaitConsumer<E, M?>) {
        continuationProcessor?.visitAwait(e, awaitConsumer)
    }

    override fun <M, E : Element<*, *>> visitSuspending(e: E, suspendConsumer: SuspendConsumer<E, M?>) {
        continuationProcessor?.visitSuspending(e, suspendConsumer)
    }

    fun findLast(): HtmlContinuation? {
        var node = continuationProcessor?.first ?: return null
        while (node.next != null) {
            node = node.next
        }
        return node
    }
}
