/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow.visitor;

import java.util.concurrent.CompletableFuture;

public abstract class HtmlVisitorAsync extends HtmlVisitor {

    /** Auxiliary for clone. */
    protected HtmlVisitorAsync(Appendable out, boolean isIndented) {
        super(out, isIndented);
    }

    @Override
    public abstract HtmlVisitorAsync clone(boolean isIndented);

    /**
     * Clones the current HtmlVisitorAsync instance with a new Appendable output. This method is used
     * to create a new instance for each rendering operation to ensure thread safety in asynchronous
     * contexts.
     *
     * @param out The new Appendable output for the cloned visitor.
     * @return A new instance of HtmlVisitorAsync with the specified output.
     */
    public abstract HtmlVisitorAsync clone(Appendable out);

    /**
     * Asynchronously finishes the rendering of the HTML view with the provided model. This method
     * should be implemented to handle the asynchronous rendering process.
     *
     * @param model The model object to be used for rendering.
     * @return A CompletableFuture that will complete when the rendering is finished.
     */
    public abstract CompletableFuture<Void> finishedAsync(Object model);
}
