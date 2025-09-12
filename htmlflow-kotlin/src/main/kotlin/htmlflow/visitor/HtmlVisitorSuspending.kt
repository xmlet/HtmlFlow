package htmlflow.visitor

/** @author Bernardo Pereira */
abstract class HtmlVisitorSuspending protected constructor(out: Appendable?, isIndented: Boolean) :
    HtmlVisitor(out, isIndented) {

    abstract override fun clone(isIndented: Boolean): HtmlVisitorSuspending

    /**
     * Clones the current HtmlVisitorSuspending instance with a new Appendable output. This method
     * is used to create a new instance for each rendering operation to ensure thread safety in
     * asynchronous contexts.
     *
     * @param out The new Appendable output for the cloned visitor.
     * @return A new instance of HtmlVisitorSuspending with the specified output.
     */
    abstract fun clone(out: Appendable): HtmlVisitorSuspending

    /**
     * Executes the rendering of the HTML view with the provided model object in a suspending
     * context.
     *
     * This method is designed to be used with Kotlin coroutines.
     *
     * @param model The model object to be used for rendering.
     */
    abstract suspend fun executeSuspending(model: Any?)
}
