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

import io.reactivex.rxjava3.core.Observable;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Text;

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
 * @author Miguel Gamboa, Luís Duare
 *         created on 17-01-2018
 */
public abstract class HtmlViewVisitor extends HtmlVisitor {
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
     * Adds a new line and indentation.
     * Checks whether the parent element is still opened or not (!isClosed).
     * If it is open then it closes the parent begin tag with ">" (!isClosed).
     * REMARK intentionally duplicating this method in other HtmlVisitor implementations,
     * to improve performance.
     */
    private final void newlineAndIndent(){
        /*
         * DO NOT REFACTOR this if (isWriting()).
         * Trying remove if (isWriting()) of newlineAndIndent() and
         * move it to call sites:  if (isWriting()){ newwlineAndIndent(); ... }
         * DEGRADES performance.
         */
        if (isWriting()){ // Keep it. DO NOT REFACTOR this if (isWriting()).
            if (isClosed){
                if(isIndented) {
                    write(Indentation.tabs(depth)); // \n\t\t\t\...
                }
            } else {
                depth++;
                if(isIndented)
                    write(Indentation.closedTabs(depth)); // >\n\t\t\t\...
                else
                    write(Tags.FINISH_TAG);
                isClosed = true;
            }
        }
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
     * While the static blocks are not in staticBlocksList then it appends elements to
     * the main StringBuilder or PrintStream.
     * Once already in staticBlocksList then it does nothing.
     * This method appends the String {@code "<elementName"} and it leaves the element
     * open to include additional attributes.
     * Before that it may close the parent begin tag with {@code ">"} if it is
     * still opened (!isClosed).
     * The newlineAndIndent() is responsible for this job to check whether the parent element
     * is still opened or not.
     *
     * @param element
     */
    @Override
    public final void visitElement(Element element) {
        newlineAndIndent();
        if (isWriting()){
            beginTag(element.getName()); // "<elementName"
            isClosed = false;
        }
    }

    /**
     * Writes the end tag for elementName: {@code "</elementName>."}.
     * This visit occurs when the º() is invoked.
     */
    @Override
    public final void visitParent(Element element) {
        if (isWriting()){
            depth--;
            newlineAndIndent();
            endTag(element.getName()); // </elementName>
        }
    }
    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr.
     * This method is invoked by visitParent specialization methods (at the end of this class)
     * for each void element such as area, base, etc.
     */
    @Override
    protected final void visitParentOnVoidElements(){
        if (!isClosed){
            write(Tags.FINISH_TAG);
        }
        isClosed = true;
    }

    @Override
    public final void visitAttribute(String attributeName, String attributeValue) {
        if (isWriting()){
            addAttribute(attributeName, attributeValue);
        }
    }

    @Override
    public final <R> void visitText(Text<? extends Element, R> text) {
        newlineAndIndent();
        if (isWriting()){
            write(text.getValue());
        }
    }


    @Override
    public final <R> void visitComment(Text<? extends Element, R> text) {
        newlineAndIndent();
        if (isWriting()){
            addComment(text.getValue());
        }
    }

    /**
     * Copies from or to staticBlocksList depending on whether the content is already stored, or not.
     * Copying from staticBlocksList will write through write() method.
     * Copying to staticBlocksList will read from substring().
     */
    @Override
    public final void visitOpenDynamic(){
        if (openDynamic )
            throw new IllegalStateException("You are already in a dynamic block! Do not use dynamic() chained inside another dynamic!");
        openDynamic = true;
        if (isStaticPartResolved){
            HtmlBlockInfo block = staticBlocksList.get(staticBlocksIndex);
            this.write(block.html);
            this.depth = block.currentDepth;
            this.isClosed = block.isClosed;
            ++staticBlocksIndex;
        } else {
            String staticBlock = substring(staticBlockIndex);
            staticBlocksList.add(new HtmlBlockInfo(staticBlock, depth, isClosed));
        }
    }

    @Override
    public final void visitCloseDynamic(){
        openDynamic = false;
        if (!isStaticPartResolved){
            staticBlockIndex = size();
        }
    }
    @Override
    public final String finished(){
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
    public <E extends Element, T> void visitAsync(Supplier<E> element, BiConsumer<E, Observable<T>> asyncAction, Observable<T> obs) {
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
