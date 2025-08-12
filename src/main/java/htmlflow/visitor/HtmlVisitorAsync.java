package htmlflow.visitor;

import java.util.concurrent.CompletableFuture;

public abstract class HtmlVisitorAsync extends HtmlVisitor {

    /**
     * Auxiliary for clone.
     */
    protected HtmlVisitorAsync(Appendable out, boolean isIndented) {
        super(out, isIndented);
    }

    @Override
    public abstract HtmlVisitorAsync clone(boolean isIndented);

    /**
     * Clones the current HtmlVisitorAsync instance with a new Appendable output.
     * This method is used to create a new instance for each rendering operation
     * to ensure thread safety in asynchronous contexts.
     *
     * @param out The new Appendable output for the cloned visitor.
     * @return A new instance of HtmlVisitorAsync with the specified output.
     */
    public abstract HtmlVisitorAsync clone(Appendable out);

    /**
     * Asynchronously finishes the rendering of the HTML view with the provided model.
     * This method should be implemented to handle the asynchronous rendering process.
     *
     * @param model The model object to be used for rendering.
     * @return A CompletableFuture that will complete when the rendering is finished.
     */
    public abstract CompletableFuture<Void> finishedAsync(Object model);
}
