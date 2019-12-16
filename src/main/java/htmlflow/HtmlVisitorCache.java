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

package htmlflow;

import org.xmlet.htmlapifaster.Area;
import org.xmlet.htmlapifaster.Base;
import org.xmlet.htmlapifaster.Br;
import org.xmlet.htmlapifaster.Col;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.Embed;
import org.xmlet.htmlapifaster.Hr;
import org.xmlet.htmlapifaster.Img;
import org.xmlet.htmlapifaster.Input;
import org.xmlet.htmlapifaster.Link;
import org.xmlet.htmlapifaster.Meta;
import org.xmlet.htmlapifaster.Param;
import org.xmlet.htmlapifaster.Root;
import org.xmlet.htmlapifaster.Source;
import org.xmlet.htmlapifaster.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base implementation of the ElementVisitor (from HtmlApiFaster
 * library) which collects information about visited Html elements of a HtmlView.
 * The HTML static content is collected into an internal cached cacheBlocksList.
 * Static content is interleaved with dynamic content.
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 17-01-2018
 */
public abstract class HtmlVisitorCache extends ElementVisitor {
    /**
     * The begin index of a static HTML block.
     */
    private int staticBlockIndex = 0;
    /**
     * keep track of current indentation.
     */
    int depth;
    /**
     * If the begin tag is closed, or not, i.e. if it is {@code "<elem>"} or it is {@code "<elem"}.
     * On element visit the begin tag is left open to include additional attributes.
     */
    private boolean isClosed = true;
    /**
     * Signals the begin of a dynamic partial view and thus it should stop
     * collecting the HTML into the cache.
     */
    private boolean openDynamic = false;
    /**
     * True when the first visit is finished and all static blocks of HTML
     * are cached in cacheBlocksList.
     */
    private boolean isCached = false;
    /**
     * A cache list of static html blocks.
     */
    private final List<HtmlBlockInfo> cacheBlocksList = new ArrayList<>();
    /**
     * The current index in cacheBlocksList corresponding to a static HTML block.
     */
    private int cacheIndex = 0;

    /**
     * If this visitor enables or not dynamic blocks.
     * If it is not dynamic and there is a dynamic block visit then it should throw
     * an Exception.
     */
    final boolean isDynamic;
    /**
     * It the HTML output should be indented or not.
     */
    final boolean isIndented;

    HtmlVisitorCache(boolean isDynamic, boolean isIndented) {
        this.isDynamic = isDynamic;
        this.isIndented = isIndented;
    }

    /**
     * This visitor may be writing to output or not, depending on the kind of HTML
     * block that it is being visited.
     * So, it should just write to output immediately only when it is:
     *   1. in a static block that is not already in cache,
     * or
     *   2 in a dynamic block that is never cached and thus must be always freshly
     *   written to the output.
     */
    public final boolean isWriting() {
        return !isCached || openDynamic;
    }
    /**
     * While the static blocks are not in cache then it appends elements to
     * the main StringBuilder or PrintStream.
     * Once already cached then it does nothing.
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
     * Copies from or to cacheBlocksList depending on whether the content is in cache or not.
     * Copying from cacheBlocksList will write through write() method.
     * Copying to cacheBlocksList will read from substring().
     */
    @Override
    public final void visitOpenDynamic(){
        if (!isDynamic)
            throw new IllegalStateException("Wrong use of dynamic() in a static view!");
        if (openDynamic )
            throw new IllegalStateException("You are already in a dynamic block! Do not use dynamic() chained inside another dynamic!");

        openDynamic = true;
        if (isCached){
            HtmlBlockInfo block = cacheBlocksList.get(cacheIndex);
            this.write(block.html);
            this.depth = block.currentDepth;
            this.isClosed = block.isClosed;
            ++cacheIndex;
        } else {
            String staticBlock = substring(staticBlockIndex);
            cacheBlocksList.add(new HtmlBlockInfo(staticBlock, depth, isClosed));
        }
    }

    @Override
    public final void visitCloseDynamic(){
        openDynamic = false;
        if (!isCached){
            staticBlockIndex = size();
        }
    }

    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr.
     * This method is invoked by visitParent specialization methods (at the end of this class)
     * for each void element such as area, base, etc.
     */
    private void visitParentOnVoidElements(){
        if (isWriting()){
            if (!isClosed){
                write(Tags.FINISH_TAG);
            }
            isClosed = true;
        }
    }

    /**
     * Adds a new line and indentation.
     * Checks whether the parent element is still opened or not (!isClosed).
     * If it is open then it closes the parent begin tag with ">" (!isClosed).
     */
    private void newlineAndIndent(){
        if (isWriting()){
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
     * Writes the {@code ">"} to output.
     */
    final void closeBeginTag() {
        if(!isClosed) {
            write(Tags.FINISH_TAG);
            isClosed = true;
            depth++;
        }
    }

    final String finished(){
        if (isCached && cacheIndex <= cacheBlocksList.size()){
            HtmlBlockInfo block = cacheBlocksList.get(cacheIndex);
            write(block.html);
            isClosed = block.isClosed;
            depth = block.currentDepth;
        }

        if (!isCached){
            String staticBlock = substring(staticBlockIndex);
            cacheBlocksList.add(new HtmlBlockInfo(staticBlock, depth, isClosed));
            isCached = true;
        }

        String result = readAndReset();
        cacheIndex = 0;
        return result;
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

    /**
     * Write {@code "<elementName"}.
     */
    protected abstract void beginTag(String elementName);

    /**
     * Writes {@code "</elementName>"}.
     */
    protected abstract void endTag(String elementName);

    /**
     * Writes {@code "attributeName=attributeValue"}
     */
    protected abstract void addAttribute(String attributeName, String attributeValue);

    /**
     * Writes {@code "<!--s-->"}
     */
    protected abstract void addComment(String s);

    /**
     * Writes the string text directly to the output.
     */
    protected abstract void write(String text);

    /**
     * Writes the char c directly to the output.
     */
    protected abstract void write(char c);

    /**
     * Returns a substring with the HTML content from the index staticBlockIndex
     */
    protected abstract String substring(int staticBlockIndex);

    /**
     * The number of characters written until this moment.
     */
    protected abstract int size();

    /**
     * Returns the accumulated output and clear it.
     */
    protected abstract String readAndReset();

    /**
     * Since HtmlVisitorCache is immutable this is the preferred way to create a copy of the
     * existing HtmlVisitorCache instance with a different isIndented state.
     *
     * @param isIndented If thenew visitor should indent HTML output or not.
     */
    protected abstract HtmlVisitorCache clone(boolean isIndented);

    /*=========================================================================*/
    /*------------            Root Element Methods         --------------------*/
    /*=========================================================================*/

    @Override
    public final <Z extends Element> void visitElementRoot(Root<Z> var1) { }

    @Override
    public final <Z extends Element> void visitParentRoot(Root<Z> var1) { }

    /*=========================================================================*/
    /*------------      Parent Methods for Void Elements   --------------------*/
    /*=========================================================================*/

    @Override
    public final <Z extends Element> void visitParentHr(Hr<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentEmbed(Embed<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentInput(Input<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentMeta(Meta<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentBr(Br<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentCol(Col<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentSource(Source<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentImg(Img<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentArea(Area<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentLink(Link<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentParam(Param<Z> element) {
        visitParentOnVoidElements ();
    }

    @Override
    public final <Z extends Element>  void visitParentBase(Base<Z> element) {
        visitParentOnVoidElements ();
    }

}
