package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.SuspendConsumer

/**
 * HtmlContinuation for a suspending block (i.e.SuspendConsumer) depending of an asynchronous object model.
 * The next continuation will be invoked on completion of the asynchronous object model.
 *
 * @param <E> the type of the parent HTML element received by the dynamic HTML block.
 * @param <T> the type of the template's model.
 */
@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
class HtmlContinuationSuspending<E: Element<*,*>, T>(
    currentDepth: Int,
    isClosed: Boolean,
    val element: E,
    private val consumer: SuspendConsumer<E, T?>,
    visitor: HtmlVisitor,
    next: HtmlContinuation?
) : HtmlContinuation(
        currentDepth, isClosed, visitor, next
) {

    override fun execute(model: Any?) {
        throw UnsupportedOperationException("Illegal use fo suspending continuation. This should be used with executeSuspend!")
    }

    override suspend fun executeSuspending(model: Any?) {
        if (currentDepth >= 0) {
            this.visitor.setIsClosed(isClosed)
            this.visitor.depth = currentDepth
        }
        /*
         * Call this consumer
         */
        this.consumer.run {
            element.accept(model as T?)
        }
        /*
         * Call next consumer
         */
        next.executeSuspending(model)
    }

    override fun copy(v: HtmlVisitor): HtmlContinuation {
        return HtmlContinuationSuspending(
            currentDepth,
            isClosed,
            copyElement<E?>(element, v),
            consumer,
            v,
            next?.copy(v)
        ) // call copy recursively
    }
}