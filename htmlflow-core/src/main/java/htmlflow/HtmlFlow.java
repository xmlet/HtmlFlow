/*
 * MIT License
 *
 * Copyright (c) 2014-2022, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
 * and Pedro Fialho.
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
package htmlflow;

import htmlflow.visitor.HtmlDocVisitor;
import htmlflow.visitor.HtmlViewVisitor;
import htmlflow.visitor.HtmlViewVisitorAsync;
import htmlflow.visitor.PreprocessingVisitor;
import htmlflow.visitor.PreprocessingVisitorAsync;

/**
 * Factory to create HtmlDoc or HtmlView instances corresponding to static HTMl pages or dynamic pages.
 * HtmlView objects depend on a model.
 */
public class HtmlFlow {
    /**
     * Make private constructor to forbid instantiation.
     */
    private HtmlFlow() {
    }
    /**
     * This will invoke the HtmlTemplate to a PreprocessingVisitor that collects a chain of
     * HtmlContinuation objects containing the static HTML strings and dynamic HTML consumers.
     *
     * @param template An HtmlTemplate function, which depends on an HtmlView used to create HTMl elements.
     * @param isIndented Set indentation on or off.
     */
    private static PreprocessingVisitor preprocessing(HtmlTemplate template, boolean isIndented) {
        PreprocessingVisitor pre = new PreprocessingVisitor(isIndented);
        HtmlView<?> preView = new HtmlView<>(() -> pre, template, false);
        template.resolve(preView); // ?????? Why ????? This is not the same as next statement ????
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }

    /**
     * @param template An HtmlTemplate function, which depends on an HtmlView used to create HTMl elements.
     * @param isIndented Set indentation on or off.
     */
    private static PreprocessingVisitorAsync preprocessingAsync(HtmlTemplate template, boolean isIndented) {
        PreprocessingVisitorAsync pre = new PreprocessingVisitorAsync(isIndented);
        HtmlView<?> preView = new HtmlView<>(() -> pre, template, false);
        template.resolve(preView); //  ?????? Why  ????? This is not the same as next statement ????
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }
    
    /**
     * Creates a HtmlDoc object corresponding to a static HTML page (without model dependency)
     * that emits HTML to an output Appendable
     *
     * @param out The output Appendable
     */
    public static HtmlDoc doc(Appendable out){
        return new HtmlDoc(new HtmlDocVisitor(out, true));
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and
     * default indentation on.
     *
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(Appendable out, HtmlTemplate template) {
        return HtmlFlow.view(out, template, true, false);
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param threadSafe Enable the use for thread safety.
     * @param <M> Type of the model rendered with this view.
     */
    static <M> HtmlView<M> view(Appendable out, HtmlTemplate template, boolean isIndented, boolean threadSafe){
        PreprocessingVisitor pre = preprocessing(template, isIndented);
        return new HtmlView<>(
            (() -> new HtmlViewVisitor(out, isIndented, pre.getFirst())),
            template,
            threadSafe);
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and
     * default indentation on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(HtmlTemplate template) {
        return HtmlFlow.view(template, true, false);
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param threadSafe Enable the use for thread safety.
     * @param <M> Type of the model rendered with this view.
     */
    static <M> HtmlView<M> view(HtmlTemplate template, boolean isIndented, boolean threadSafe){
        PreprocessingVisitor pre = preprocessing(template, isIndented);
        return new HtmlView<>(
                (() -> new HtmlViewVisitor(new StringBuilder(), isIndented, pre.getFirst())),
                template,
                threadSafe);
    }
    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model and
     * default indentation on.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template) {
        return HtmlFlow.viewAsync(template, true, false);
    }
    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param <M> Type of the model rendered with this view.
     */
    static <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template, boolean isIndented, boolean threadSafe){
        PreprocessingVisitorAsync pre = preprocessingAsync(template, isIndented);
        return new HtmlViewAsync<>(new HtmlViewVisitorAsync(isIndented, pre.getFirst()), template, threadSafe);
    }
}
