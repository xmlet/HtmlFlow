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

import htmlflow.visitor.HtmlDocVisitorPrintStream;
import htmlflow.visitor.HtmlDocVisitorStringBuilder;
import htmlflow.visitor.HtmlViewVisitorPrintStream;
import htmlflow.visitor.HtmlViewVisitorStringBuilder;
import htmlflow.visitor.HtmlViewVisitorAsync;
import htmlflow.visitor.PreprocessingVisitor;
import htmlflow.visitor.PreprocessingVisitorAsync;

import java.io.PrintStream;
import java.lang.reflect.Type;

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
     * @param modelClass The class of the model.
     * @param template An HtmlTemplate function, which depends on an HtmlView used to create HTMl elements.
     * @param <U> The type of the model.
     */
    private static <U> PreprocessingVisitor<U> preprocessing(
        HtmlTemplate<U> template,
        Class<?> modelClass,
        Type... genericTypeArgs
    ) {
        PreprocessingVisitor<U> pre = new PreprocessingVisitor<>(true, modelClass, genericTypeArgs);
        HtmlView<U> preView = new HtmlView<>(null, () -> pre, false);
        template.resolve(preView);
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().finish(null);
        return pre;
    }
    
    private static <U> PreprocessingVisitorAsync<U> preprocessingAsync(
            HtmlTemplate<U> template,
            Class<?> modelClass,
            Type... genericTypeArgs
    ) {
        PreprocessingVisitorAsync<U> pre = new PreprocessingVisitorAsync<>(true, modelClass, genericTypeArgs);
        HtmlView<U> preView = new HtmlView<>(null, () -> pre, false);
        template.resolve(preView);
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().finish(null);
        return pre;
    }
    
    /**
     * Creates a HtmlDoc object corresponding to a static HTML page (without model dependency)
     * that emits HTML to an output PrintStream
     *
     * @param out The output Printstream
     */
    public static HtmlDoc doc(PrintStream out){
        return out == null
            ? new HtmlDoc(null, new HtmlDocVisitorStringBuilder(true))
            : new HtmlDoc(out, new HtmlDocVisitorPrintStream(out, true));
    }
    /**
     * Creates a HtmlDoc object corresponding to a static HTML page (without model dependency)
     * that emits HTML to and internal StringBuilder that provides the resulting String on
     * render() of HtmDoc.
     */
    public static HtmlDoc doc(){
        return doc(null);
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param modelClass Used to crate fake model object for preprocessing of HtmlTemplate.
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <U> Type of the model.
     */
    public static <U> HtmlView<U> view(
        PrintStream out,
        HtmlTemplate<U> template,
        Class<?> modelClass,  // Cannot replace wildcard by U due to generic collections.
        Type... genericTypeArgs
    ){
        PreprocessingVisitor<U> pre = preprocessing(template, modelClass, genericTypeArgs);
        return new HtmlView<>(
            out,
            (() -> new HtmlViewVisitorPrintStream<>(out, true, pre.getFirst())),
            false); // not thread safe by default
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param modelClass Used to crate fake model object for preprocessing of HtmlTemplate.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <U> Type of the model.
     */
    public static <U> HtmlView<U> view(
        HtmlTemplate<U> template,
        Class<?> modelClass, // Cannot replace wildcard by U due to generic collections.
        Type... genericTypeArgs
    ){
        PreprocessingVisitor<U> pre = preprocessing(template, modelClass, genericTypeArgs);
        return new HtmlView<>(
            null, // Without output stream
            () -> new HtmlViewVisitorStringBuilder<>(true, pre.getFirst()), // visitor
            false); // Not thread safe by default
    }
    
    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model.
     *
     * @param modelClass Used to crate fake model object for preprocessing of HtmlTemplate.
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <U> Type of the model.
     */
    public static <U> HtmlViewAsync<U> viewAsync(
            PrintStream out,
            HtmlTemplate<U> template,
            Class<?> modelClass,
            Type... genericTypeArgs
    ){
        PreprocessingVisitorAsync<U> pre = preprocessingAsync(template, modelClass, genericTypeArgs);
        return new HtmlViewAsync<>(
                out,
                () -> new HtmlViewVisitorAsync<>(out, true, pre.getFirst(), pre.getCf()),
                false);
    }
}
