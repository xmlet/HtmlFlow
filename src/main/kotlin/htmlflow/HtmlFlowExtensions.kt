package htmlflow

import org.xmlet.htmlapifaster.Element

val <T : Element<*,*>, Z : Element<*,*>> Element<T, Z>.l: Z
    get() = this.`__`()

/**
 * @param T type of the Element receiver
 * @param Z type of the parent Element of the receiver
 * @param M type of the model (aka context object)
 */
fun <T : Element<*,*>, Z : Element<*,*>, M> Element<T, Z>.dyn(cons: T.(M) -> Unit): T {
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
