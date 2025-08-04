package htmlflow

//import jdk.internal.org.jline.utils.ShutdownHooks
import htmlflow.continuations.HtmlContinuationSuspendableTerminationNode
import htmlflow.visitor.HtmlVisitor
import htmlflow.visitor.PreprocessingVisitor
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.Html
import org.xmlet.htmlapifaster.Text

/**
 * Alternative close tag function for `__()`.
 */
inline val <T : Element<*, Z>, Z : Element<*,*>> T.l: Z
    get() = this.`__`()

/**
 * Root property of HTML element.
 */
inline val HtmlPage.html: Html<HtmlPage>
    get() {
        (this.visitor as HtmlVisitor).write(HtmlPage.HEADER)
        return Html(self())
    }

/**
 * Root builder of HTML element with lambda with receiver.
 */
inline fun HtmlPage.html(block : Html<HtmlPage>.() -> Unit) : HtmlPage {
    (this.visitor as HtmlVisitor).write(HtmlPage.HEADER)
    return Html(self()).also { it.block() }.l
}

/**
 * Text node property.
 */
inline var <T : Element<T,Z>, Z : Element<*,*>> T.text : T
    get() = self()
    set(value) {
        visitor.visitText(
            Text(
                self(),
                visitor,
                value,
            )
        )
    }

/**
 * @param T type of the Element receiver
 * @param Z type of the parent Element of the receiver
 * @param M type of the model (aka context object)
 */
inline fun <T : Element<*,*>, Z : Element<*,*>, M> Element<T, Z>.dyn(crossinline cons: T.(M) -> Unit): T {
    this.dynamic<M> { elem, model ->
        elem.cons(model)
    }
    return this.self()
}

/**
 * @param T type of the Element receiver
 * @param Z type of the parent Element of the receiver
 * @param M type of the model (aka context object)
 */
inline fun <T : Element<*,*>, Z : Element<*,*>, M> Element<T, Z>.await(crossinline cons: T.(M, () -> Unit) -> Unit): T {
    this.await<M> { elem, model, cb ->
        elem.cons(model) { cb.finish() }
    }
    return this.self()
}

/**
 * Appendable extension to build an HtmlDoc.
 */
fun Appendable.doc(block: HtmlDoc.() -> Unit): HtmlDoc = HtmlFlow.doc(this).also(block)

/**
 * @param M type of the model (aka context object)
 */
fun <M : Any?> view(template: HtmlPage.() -> Unit) = HtmlFlow.view<M>(template)

/**
 * @param M type of the model (aka context object)
 */
fun <M : Any?> viewAsync(template: HtmlPage.() -> Unit) = HtmlFlow.viewAsync<M>(template)

/**
 * @param M type of the model (aka context object)
 */
fun <M : Any?> viewSuspend(template: HtmlPage.() -> Unit) = viewSuspend<M>(
    template,
    isIndented = true,
    threadSafe = false
)

fun <M : Any?> viewSuspend(template: HtmlPage.() -> Unit, isIndented: Boolean, threadSafe: Boolean): HtmlViewSuspend<M> {
    val pre = preprocessingSuspend(template, isIndented)
    val visitor = HtmlViewVisitorSuspend(
        isIndented = isIndented,
        first = pre.first
    )
    /**
     * Chain terminationNode next to the last node.
     */
    val terminationNode = HtmlContinuationSuspendableTerminationNode()
    PreprocessingVisitor.HtmlContinuationSetter.setNext(visitor.findLast(), terminationNode)
    return HtmlViewSuspend(template, visitor, threadSafe)
}

/**
 * @param template An HtmlTemplate function, which depends on an HtmlView used to create HTMl elements.
 * @param isIndented Set indentation on or off.
 */
private fun preprocessingSuspend(template: HtmlTemplate, isIndented: Boolean): PreprocessingVisitorSuspend {
    val pre = PreprocessingVisitorSuspend(isIndented)
    val preView: HtmlView<*> = HtmlView<Any>({ pre }, template, false)
    template.resolve(preView) // ????? Why ?????
    /**
     * NO problem with null model. We are just preprocessing static HTML blocks.
     * Thus, dynamic blocks which depend on model are not invoked.
     */
    preView.visitor.resolve(null)
    return pre
}
