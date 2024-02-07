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
 * @param M type of the model (aka context object)
 */
fun <M : Any?> view(template: HtmlPage.() -> Unit) = HtmlFlow.view<M>(template)

/**
 * @param M type of the model (aka context object)
 */
fun <M : Any?> viewAsync(template: HtmlPage.() -> Unit) = HtmlFlow.viewAsync<M>(template)
