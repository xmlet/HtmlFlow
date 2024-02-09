package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor
import kotlinx.coroutines.future.await
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.async.AwaitConsumer
import java.lang.UnsupportedOperationException
import java.util.concurrent.CompletableFuture


@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
class HtmlContinuationSuspendableAsync<E: Element<*, *>, T>(
    currentDepth: Int,
    isClosed: Boolean,
    private val element: E,
    private val consumer: AwaitConsumer<E, T>,
    visitor: HtmlVisitor,
    next: HtmlContinuation?
) : HtmlContinuation(
    currentDepth, isClosed, visitor, next
) {
    override suspend fun executeSuspending(model: Any?) {
        if (currentDepth >= 0) {
            visitor.setIsClosed(isClosed)
            visitor.depth = currentDepth
        }
        val cf = CompletableFuture<Unit>()
        consumer.accept(element, model as T) {
            cf.complete(null)
        }
        cf.await()
        next?.executeSuspending(model)
    }

    override fun execute(model: Any?) {
        throw UnsupportedOperationException("Illegal use of execute in suspendable continuation! Should use executeSuspending.")
    }

    override fun copy(v: HtmlVisitor): HtmlContinuation? {
        return HtmlContinuationSuspendableAsync (
            currentDepth,
            isClosed,
            copyElement(element, v),
            consumer,
            v,
            next?.copy(v)
        ) // call copy recursively
    }
}