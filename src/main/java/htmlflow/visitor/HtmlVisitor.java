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

import static htmlflow.visitor.Tags.*;

import htmlflow.HtmlMfeConfig;
import htmlflow.exceptions.HtmlFlowAppendException;
import htmlflow.visitor.escape.HtmlEscapers;
import java.io.IOException;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.xmlet.htmlapifaster.*;

/**
 * This is the base implementation of the ElementVisitor (from HtmlApiFaster library).
 *
 * @author Miguel Gamboa created on 04-08-2022
 */
public abstract class HtmlVisitor extends ElementVisitor {

    protected Appendable out;

    /** keep track of current indentation. */
    protected int depth;

    /**
     * If the beginning tag is closed, or not, i.e. if it is {@code "<elem>"} or it is {@code
     * "<elem"}. On element visit the beginning tag is left open to include additional attributes.
     */
    protected boolean isClosed = true;

    /** It the HTML output should be indented or not. */
    public final boolean isIndented;

    private final List<HtmlMfeConfig> mfePage = new ArrayList<>();

    public void addMfePage(HtmlMfeConfig mfePage) {
        this.mfePage.add(mfePage);
    }

    public List<HtmlMfeConfig> getMfePage() {
        return mfePage;
    }

    /** keep track of current indentation. */
    public final int getDepth() {
        return depth;
    }

    /** Set current indentation. */
    public final void setDepth(int v) {
        depth = v;
    }

    public final void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    protected HtmlVisitor(Appendable out, boolean isIndented) {
        this.out = out;
        this.isIndented = isIndented;
    }

    public final Appendable out() {
        return out;
    }

    public final HtmlVisitor setAppendable(Appendable appendable) {
        this.out = appendable;
        return this;
    }

    public final void write(String text) {
        try {
            this.out.append(text);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    protected final void write(char c) {
        try {
            this.out.append(c);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Adds a new line and indentation. Checks whether the parent element is still opened or not
     * (!isClosed). If it is open then it closes the parent begin tag with ">" (!isClosed).
     */
    public final void newlineAndIndent() {
        if (isClosed) {
            if (isIndented) {
                write(Indentation.tabs(depth)); // \n\t\t\t\...
            }
        } else {
            depth++;
            if (isIndented) write(Indentation.closedTabs(depth)); // >\n\t\t\t\...
            else write(FINISH_TAG);
            isClosed = true;
        }
    }

    /**
     * This method appends the String {@code "<elementName"} and it leaves the element open to include
     * additional attributes. Before that it may close the parent begin tag with {@code ">"} if it is
     * still opened (!isClosed). The newlineAndIndent() is responsible for this job to check whether
     * the parent element is still opened or not.
     *
     * @param element
     */
    @Override
    public final void visitElement(Element element) {
        newlineAndIndent();
        beginTag(out, element.getName()); // "<elementName"
        isClosed = false;
    }

    /**
     * Writes the end tag for elementName: {@code "</elementName>."}. This visit occurs when the º()
     * is invoked.
     */
    @Override
    public final void visitParent(Element element) {
        depth--;
        newlineAndIndent();
        endTag(out, element.getName()); // </elementName>
    }

    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track,
     * wbr. This method is invoked by visitParent specialization methods (at the end of this class)
     * for each void element such as area, base, etc.
     */
    protected final void visitParentOnVoidElements() {
        if (!isClosed) {
            write(FINISH_TAG);
        }
        isClosed = true;
    }

    @Override
    public final void visitAttribute(
        String attributeName,
        String attributeValue
    ) {
        if (isClosed) throw new IllegalStateException(
            "Cannot add attributes after!!!"
        );
        addAttribute(out, attributeName, attributeValue);
    }

    /**
     * To distinguish boolean attributes from others.
     *
     * @param name Attribute name
     * @param value Attribute value
     */
    @Override
    public final void visitAttributeBoolean(String name, String value) {
        if (!value.equals("false")) addAttribute(out, name, value);
    }

    @Override
    public final <R> void visitText(Text<? extends Element, R> text) {
        newlineAndIndent();
        write(HtmlEscapers.htmlEscaper().escape(text.getValue()));
    }

    /**
     * To distinguish from text() that escapes HTML by default. This raw() acts like text() but
     * keeping text as it is.
     */
    @Override
    public final <R> void visitRaw(Text<? extends Element, R> text) {
        newlineAndIndent();
        write(text.getValue());
    }

    @Override
    public final <R> void visitComment(Text<? extends Element, R> text) {
        newlineAndIndent();
        addComment(out, text.getValue());
    }

    @Override
    public <E extends Element> void visitMfe(E e, Consumer<MfeConfiguration> mfeConsumerCfg) {
        // collect the mfe configuration
        HtmlMfeConfig mfeConfig = new HtmlMfeConfig();
        mfeConsumerCfg.accept(mfeConfig);
        addMfePage(mfeConfig);

        e.custom(mfeConfig.getMfeElementName()).addAttr("mfe-url", mfeConfig.getMfeUrlResource());
        e.getVisitor().visitAttribute("mfe-name", mfeConfig.getMfeName());
        e.getVisitor().visitAttribute("mfe-styling-url", mfeConfig.getMfeStylingUrl());
        e.getVisitor().visitAttribute("mfe-listen-event", mfeConfig.getMfeListeningEventName());
        e.getVisitor().visitAttribute("mfe-trigger-event", mfeConfig.getMfeTriggerEventName());
        if(mfeConfig.isMfeStreamingData()){
            e.getVisitor().visitAttribute("mfe-stream-data", String.valueOf(mfeConfig.isMfeStreamingData()));
        }
        e.custom("/" + mfeConfig.getMfeElementName());
    }

    /*=========================================================================*/
    /*------------            Abstract HOOK Methods         -------------------*/
    /*=========================================================================*/
    /** Processing output. */
    public abstract void resolve(Object model);

    /**
     * Since HtmlVisitor is immutable this is the preferred way to create a copy of the existing
     * HtmlVisitor instance with a different isIndented state.
     *
     * @param isIndented If the new visitor should indent HTML output or not.
     */
    public abstract HtmlVisitor clone(boolean isIndented);

    /*=========================================================================*/
    /*------------            Root Element Methods         --------------------*/
    /*=========================================================================*/

    @Override
    public final <Z extends Element> void visitElementRoot(Root<Z> var1) {}

    @Override
    public final <Z extends Element> void visitParentRoot(Root<Z> var1) {}

    /*=========================================================================*/
    /*------------      Parent Methods for Void Elements   --------------------*/
    /*=========================================================================*/

    @Override
    public final <Z extends Element> void visitParentHr(Hr<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentEmbed(Embed<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentInput(Input<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentMeta(Meta<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentBr(Br<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentCol(Col<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentSource(Source<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentImg(Img<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentArea(Area<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentLink(Link<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentParam(Param<Z> element) {
        visitParentOnVoidElements();
    }

    @Override
    public final <Z extends Element> void visitParentBase(Base<Z> element) {
        visitParentOnVoidElements();
    }
}
