package htmlflow

import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer

/**
 * @param E This HTML element
 */
suspend fun <E: Element<*,*>> E.suspending(block: suspend E.() -> Unit): E {
    if(lookForRootElement() !is HtmlDoc) {
        throw IllegalStateException("Illegal use of suspending. Using .suspending { elem -> ...} without a model, is only valid in HtmlDoc.")
    }
    block(this)
    return this
}

/**
 * @param E This HTML element
 * @param M The type of model.
 */
fun <E: Element<*,*>, M> E.suspending(block: suspend E.(M) -> Unit) : E {
    /*
     * This verification is made in preprocessing using a dummy HtmlView for both sync and async cases.
     */
    if(lookForRootElement() is HtmlDoc) {
        throw IllegalStateException("Illegal use of suspending in HtmlDoc. Using .suspending { elem, model -> ...} is only valid in HtmlViewAsync.")
    }

    this.visitor.visitSuspending(this, object : SuspendConsumer<E,M> {
        override suspend fun E.accept(model: M) {
            block(this, model as M)
        }
    })
    return this
}

fun <E: Element<*,*>> E.lookForRootElement() : HtmlPage {
    if(this is HtmlPage) {
        return this
    }
    return this.parent.lookForRootElement()
}