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
     */
    private static PreprocessingVisitor preprocessing(HtmlTemplate template) {
        PreprocessingVisitor pre = new PreprocessingVisitor(true);
        HtmlView preView = new HtmlView(() -> pre, false);
        template.resolve(preView);
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }
    
    private static PreprocessingVisitorAsync preprocessingAsync(HtmlTemplate template) {
        PreprocessingVisitorAsync pre = new PreprocessingVisitorAsync(true);
        HtmlView preView = new HtmlView(() -> pre, false);
        template.resolve(preView);
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
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     */
    public static HtmlView view(Appendable out, HtmlTemplate template){
        PreprocessingVisitor pre = preprocessing(template);
        return new HtmlView(
            (() -> new HtmlViewVisitor(out, true, pre.getFirst())),
            false); // not thread safe by default
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     */
    public static HtmlView view(HtmlTemplate template){
        PreprocessingVisitor pre = preprocessing(template);
        return new HtmlView(
                (() -> new HtmlViewVisitor(new StringBuilder(), true, pre.getFirst())),
                false); // not thread safe by default
    }
    
    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     */
    public static HtmlViewAsync viewAsync(HtmlTemplate template){
        PreprocessingVisitorAsync pre = preprocessingAsync(template);
        return new HtmlViewAsync(new HtmlViewVisitorAsync(true, pre.getFirst()));
    }
}
