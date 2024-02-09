package htmlflow

import org.xmlet.htmlapifaster.Element

inline val <T : Element<*, Z>, Z : Element<*,*>> T.l: Z
    get() = this.`__`()

inline fun <T : Element<*, Z>, Z : Element<*,*>> T.add(block: T.() -> Unit): T {
    block()
    return this
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
