package htmlflow.continuations

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
open class HtmlContinuationSuspendable {
    open suspend fun executeSuspending(model: Any?): Unit =
        throw UnsupportedOperationException("Illegal use of executeSuspending!")
}
