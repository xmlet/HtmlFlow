/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
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
import org.reactivestreams.Publisher;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.OnPublisherCompletion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


/**
 * This is the base implementation of the ElementVisitor (from HtmlApiFaster
 * library) which collects information about visited Html elements of a HtmlView.
 * The HTML static content is collected into an internal staticBlocksList.
 * Static content is interleaved with dynamic content.
 *
 * @param <T> The type of domain object (i.e. the model)
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 17-01-2018
 */
public abstract class HtmlViewVisitor<T> extends HtmlVisitor {
    /**
     * The begin index of a static HTML block.
     */
    private int staticBlockIndex = 0;
    /**
     * Signals the begin of a dynamic partial view and thus it should stop
     * collecting the HTML into the staticBlocksList.
     */
    private boolean openDynamic = false;
    /**
     * True when the first visit is finished and all static blocks of HTML
     * are in staticBlocksList.
     */
    private boolean isStaticPartResolved = false;
    /**
     * A list of static html blocks.
     */
    private final List<HtmlBlockInfo> staticBlocksList = new ArrayList<>();
    /**
     * The current index in staticBlocksList corresponding to a static HTML block.
     */
    private int staticBlocksIndex = 0;

    HtmlViewVisitor(boolean isIndented) {
        super(isIndented);
    }
    /**
     * This visitor may be writing to output or not, depending on the kind of HTML
     * block that it is being visited.
     * So, it should just write to output immediately only when it is:
     *   1. in a static block that is not already in staticBlocksList,
     * or
     *   2 in a dynamic block that is never stored in staticBlocksList and
     *   thus must be always freshly written to the output.
     */
    public final boolean isWriting() {
        return !isStaticPartResolved || openDynamic;
    }

    /**
     * Here we are writing the previous static HTML block and the next dynamicHtmlBlock.
     *
     * @param dynamicHtmlBlock The continuation that consumes the element and a model.
     */
    @Override
    public final <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> dynamicHtmlBlock) {
        if (openDynamic )
            throw new IllegalStateException("You are already in a dynamic block! Do not use dynamic() chained inside another dynamic!");
        if (isStaticPartResolved){
            /**
             * Writing the previous static HTML block.
             */
            HtmlBlockInfo block = staticBlocksList.get(staticBlocksIndex);
            this.write(block.html);
            /**
             * Prepare for next dynamic block.
             */
            this.depth = block.currentDepth;
            this.isClosed = block.isClosed;
            ++staticBlocksIndex;
        } else {
            /**
             * Storing the previous static HTML block that has been already written.
             */
            String staticBlock = substring(staticBlockIndex);
            staticBlocksList.add(new HtmlBlockInfo(staticBlock, depth, isClosed));
        }
        /**
         * Write the next dynamic HTML block.
         */
        openDynamic = true;
        dynamicHtmlBlock.accept(element, null);
        openDynamic = false;
        staticBlockIndex = size();
    }

    public final String finished(T model, HtmlView...partials){
        if (isStaticPartResolved && staticBlocksIndex <= staticBlocksList.size()){
            HtmlBlockInfo block = staticBlocksList.get(staticBlocksIndex);
            write(block.html);
            isClosed = block.isClosed;
            depth = block.currentDepth;
        }

        if (!isStaticPartResolved){
            String staticBlock = substring(staticBlockIndex);
            staticBlocksList.add(new HtmlBlockInfo(staticBlock, depth, isClosed));
            isStaticPartResolved = true;
        }

        String result = readAndReset();
        staticBlocksIndex = 0;
        return result;
    }
    @Override
    public <E extends Element, T> OnPublisherCompletion visitAsync(Supplier<E> element, BiConsumer<E, Publisher<T>> asyncAction, Publisher<T> obs) {
        throw new IllegalStateException("Wrong use of async() in a HtmlView! Use HtmlFlow.viewAsync() to produce an async view.");
    }

    @Override
    public <E extends Element> void visitThen(Supplier<E> elem) {
        throw new IllegalStateException("Wrong use of then() in a HtmlView! Use HtmlFlow.viewAsync() to produce an async view.");
    }

    static class HtmlBlockInfo {

        final String html;
        final int currentDepth;
        final boolean isClosed;

        HtmlBlockInfo(String html, int currentDepth, boolean isClosed){
            this.html = html;
            this.currentDepth = currentDepth;
            this.isClosed = isClosed;
        }
    }
    /*=========================================================================*/
    /*------------            Abstract HOOK Methods         -------------------*/
    /*=========================================================================*/
    /**
     * Returns a substring with the HTML content from the index staticBlockIndex
     */
    protected abstract String substring(int staticBlockIndex);
    /**
     * Returns the accumulated output and clear it.
     */
    protected abstract String readAndReset();
}
