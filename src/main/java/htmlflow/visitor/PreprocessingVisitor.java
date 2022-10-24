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

package htmlflow.visitor;

import htmlflow.HtmlView;
import org.xmlet.htmlapifaster.Element;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;

/**
 * This visitor is used to make a preprocessing resolution of an HtmlTemplate.
 * It will collect the resulting HTML from visiting static HTML elements into an auxiliary
 * StringBuilder that later is extracted to: String staticHtml = sb.substring(staticBlockIndex);
 * to create an HtmlContinuationStatic object.
 * It also interleaves the creation of HtmlContinuationDynamic nodes that only store dynamicHtmlBlock
 * objects corresponding to a BiConsumer<E, U> (being E an element and U the model).
 * The U comes from external module HtmlApiFaster whose classes are not strongly typed with the Model.
 * Thus, only the dynamic() and visitDynamic() methods in HtmlApiFaster were made generic to carry
 * a type parameter U corresponding to the type of the Model.
 * Nevertheless, this U should be corresponding to this visitor T of model that is parametrized in HtmlView.
 *
 * @param <T> The type of the Model bound to a view.
 */
public class PreprocessingVisitor<T> extends HtmlViewVisitor<T> implements TagsToStringBuilder {
    private static final String NOT_SUPPORTED_ERROR =
        "This is a PreprocessingVisitor used to compile templates and not intended to support HTML views!";
    /**
     * The main StringBuilder.
     */
    private final StringBuilder sb = new StringBuilder();
    /**
     * The internal String builder beginning index of a static HTML block.
     */
    private int staticBlockIndex = 0;
    /**
     * The first node to be processed.
     */
    private HtmlContinuation<Object> first;
    /**
     * The last HtmlContinuation
     */
    private HtmlContinuation<Object> last;
    /**
     * Used create a mocked instance of the model to be passed to dynamic HTML blocks.
     */
    private final Class<T> modelClass;

    public PreprocessingVisitor(Class<T> modelClass, boolean isIndented) {
        super(isIndented);
        this.modelClass = modelClass;
    }

    public HtmlContinuation<Object> getFirst() {
        return first;
    }

    public HtmlContinuation<Object> getLast() {
        return last;
    }

    @Override
    public void write(String text) {
        sb.append(text);
    }

    @Override
    protected void write(char c) {
        sb.append(c);
    }

    /**
     * Here we are creating 2 HtmlContinuation objects: one for previous static HTML and a next one
     * corresponding to the consumer passed to dynamic().
     * We will first create the dynamic continuation that will be the next node of the static continuation.
     *
     * @param element The parent element.
     * @param dynamicHtmlBlock The continuation that consumes the element and a model.
     */

    @Override
    public <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> dynamicHtmlBlock) {
        /**
         * Creates an HtmlContinuation for the dynamic block.
         */
        HtmlContinuation dynamicCont = new HtmlContinuationDynamic<>(depth, isClosed, element, dynamicHtmlBlock, this, null);
        /**
         * We are resolving this view for the first time.
         * Now we just need to create an HtmlContinuation corresponding to the previous static HTML,
         * which will be followed by the dynamicCont.
         */
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<Object> staticCont = new HtmlContinuationStatic<>(staticHtml, this, dynamicCont);
        if(first == null) first = staticCont; // on first visit initializes the first pointer
        else setNext(last, staticCont);       // else append the staticCont to existing chain
        last = dynamicCont;                   // advance last to point to the new dynamicCont
        /**
         * We have to run dynamicContinuation to leave isClosed and indentation correct for
         * the next static HTML block.
         */
        PodamFactory factory = new PodamFactoryImpl();
        T model = factory.manufacturePojoWithFullData(modelClass);
        dynamicHtmlBlock.accept(element, (U) model);
        staticBlockIndex = sb.length(); // increment the staticBlockIndex to the end of internal string buffer.
    }

    /**
     * Creates the last static HTML block.
     */
    @Override
    public String finish(T model, HtmlView... partials) {
        String staticHtml = sb.substring(staticBlockIndex);
        HtmlContinuation<Object> staticCont = new HtmlContinuationStatic<>(staticHtml, this, null);
        last = first == null
            ? first = staticCont         // assign both first and last
            : setNext(last, staticCont); // append new staticCont and return it to be the new last continuation.
        /**
         * We are just collecting static HTML blocks and the resulting HTML should be ignored.
         * Intentionally return null to force a NullPointerException if someone intend to use this result.
         */
        return null;
    }

    @Override
    public HtmlVisitor clone(PrintStream out, boolean isIndented) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_ERROR);
    }

    @Override
    public HtmlViewVisitor newbie() {
        throw new UnsupportedOperationException(NOT_SUPPORTED_ERROR);
    }

    @Override
    public StringBuilder sb() {
        return sb;
    }

    static class HtmlContinuationSetter {
        static final Field fieldNext;
        static {
            try {
                fieldNext = HtmlContinuation.class.getDeclaredField("next");
                fieldNext.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        static HtmlContinuation<Object> setNext(HtmlContinuation<Object> cont, HtmlContinuation next) {
            try {
                fieldNext.set(cont, next);
                return next;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
