package htmlflow.continuations

import htmlflow.visitor.HtmlVisitor
import java.util.function.BiConsumer
import org.xmlet.htmlapifaster.Element

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
class HtmlContinuationSuspendableSyncDynamic<E : Element<*, *>, T>(
    currentDepth: Int,
    isClosed: Boolean,
    /** The element passed to the continuation consumer. */
    private val element: E,
    /** The continuation that consumes the element and a model. */
    private val consumer: BiConsumer<E, T>,
    visitor: HtmlVisitor,
    next: HtmlContinuation?,
) : HtmlContinuationSuspendableSync(currentDepth, isClosed, visitor, next) {
    @Suppress("UNCHECKED_CAST")
    override fun emitHtml(model: Any?) {
        consumer.accept(element, model as T)
    }

    override fun copy(v: HtmlVisitor): HtmlContinuation {
        return HtmlContinuationSuspendableSyncDynamic<E, T>(
            currentDepth,
            isClosed,
            copyElement<E>(element, v),
            consumer,
            v,
            next?.copy(v),
        ) // call copy recursively
    }
}
