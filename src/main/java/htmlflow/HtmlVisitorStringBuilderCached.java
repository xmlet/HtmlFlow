/*
 * MIT License
 *
 * Copyright (c) 2014-16, mcarvalho (gamboa.pt)
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

import org.xmlet.htmlapifaster.ElementVisitor;

import java.util.ArrayList;
import java.util.List;

import static htmlflow.HtmlUtils.NEWLINE;
import static htmlflow.HtmlUtils.TAB;

/**
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorStringBuilderCached extends ElementVisitor {

    private static final int tabsMax;
    private static String[] tabs;
    private static String[] closedTabs;

    static {
        tabsMax = 1000;
        tabs = HtmlUtils.createTabs(tabsMax);
        closedTabs = HtmlUtils.createClosedTabs(tabsMax);
    }

    private final StringBuilder sb;
    private StringBuilder cacheBitsStorage;
    private int depth;
    private int startingDepth;
    private boolean isClosed = true;
    private boolean openDynamic = false;
    private boolean isCached = false;
    private List<PartInfo> cacheBitsList = new ArrayList<>();
    private PartInfo[] cacheBitsArray;
    private int cacheIndex = 0;

    HtmlVisitorStringBuilderCached(StringBuilder sb) {
        this.sb = sb;
        this.cacheBitsStorage = new StringBuilder(sb.toString());
    }

    @SuppressWarnings("unused")
    public HtmlVisitorStringBuilderCached(StringBuilder sb, int depth) {
        this.sb = sb;
        this.cacheBitsStorage = new StringBuilder(sb.toString());
        this.depth = depth;
        this.startingDepth = depth;

        for (int i = 0; i < depth; i++) {
            this.sb.append(TAB);
            this.cacheBitsStorage.append(TAB);
        }
    }

    @Override
    public final void visitElement(String elementName) {
        if (!isCached || openDynamic){
            if (!isClosed){
                depth++;
            }

            tabs();
            HtmlTags.appendOpenTag(sb, elementName);

            if (!isCached){
                HtmlTags.appendOpenTag(cacheBitsStorage, elementName);
            }

            isClosed = false;
        }
    }

    @Override
    public final void visitParent(String elementName) {
        if (!isCached || openDynamic){
            if (isClosed) {
                depth--;
            }

            tabs();
            HtmlTags.appendCloseTag(sb, elementName);

            if(!isCached){
                HtmlTags.appendCloseTag(cacheBitsStorage, elementName);
            }
        }
    }

    @Override
    public final void visitAttribute(String attributeName, String attributeValue) {
        if (!isCached || openDynamic){
            HtmlTags.appendAttribute(sb, attributeName, attributeValue);

            if (!isCached){
                HtmlTags.appendAttribute(cacheBitsStorage, attributeName, attributeValue);
            }
        }
    }

    /**
     * An optimized version of Text without binder.
     */
    @Override
    public final <R> void visitText(R text) {
        if (!isCached || openDynamic){
            if (!isClosed){
                depth++;
            }

            tabs();
            sb.append(text);

            if (!isCached){
                cacheBitsStorage.append(text);
            }
        }
    }

    @Override
    public final <R> void visitComment(R comment) {
        if (!isCached || openDynamic){
            if (!isClosed){
                depth++;
            }

            tabs();
            HtmlTags.appendComment(sb, comment.toString());

            if (!isCached){
                HtmlTags.appendComment(cacheBitsStorage, comment.toString());
            }
        }
    }

    @Override
    public final void visitOpenDynamic(){
        openDynamic = true;

        if (isCached){
            PartInfo partInfo = cacheBitsArray[cacheIndex];
            sb.append(partInfo.part);
            this.depth = partInfo.currentDepth;
            this.isClosed = partInfo.isClosed;
            ++cacheIndex;
        } else {
            cacheBitsList.add(new PartInfo(cacheBitsStorage.toString(), depth, isClosed));
        }
    }

    public final void visitCloseDynamic(){
        openDynamic = false;

        if (!isCached){
            cacheBitsStorage = new StringBuilder();
        }
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    private void tabs(){
        if (!isCached || openDynamic){
            if (isClosed){
                if (sb.length() != startingDepth){
                    sb.append(tabs[depth]);

                    if (!isCached){
                        cacheBitsStorage.append(tabs[depth]);
                    }
                }
            } else {
                sb.append(closedTabs[depth]);

                if (!isCached){
                    cacheBitsStorage.append(closedTabs[depth]);
                }

                isClosed = true;
            }
        }
    }

    String setTemplateDefined(){
        if (isCached && cacheIndex <= cacheBitsArray.length){
            sb.append(cacheBitsArray[cacheIndex].part);
        }

        if (!isCached){
            cacheBitsList.add(new PartInfo(cacheBitsStorage.toString(), depth, isClosed));
            cacheBitsArray = new PartInfo[cacheBitsList.size()];

            cacheBitsList.toArray(cacheBitsArray);

            isCached = true;
        }

        String result = sb.toString();

        sb.setLength(0);
        cacheIndex = 0;
        depth = startingDepth;

        return result;
    }

    void add(String innerView) {
        if (!isClosed){
            HtmlTags.appendOpenTagEnd(sb);

            if (!isCached){
                HtmlTags.appendOpenTagEnd(cacheBitsStorage);
            }

            ++depth;
        }

        sb.append(NEWLINE);
        sb.append(innerView);

        if (!isCached){
            cacheBitsStorage.append(NEWLINE);
            cacheBitsStorage.append(innerView);
        }

        isClosed = true;
    }

    class PartInfo{

        String part;
        int currentDepth;
        boolean isClosed;

        PartInfo(String part, int currentDepth, boolean isClosed){
            this.part = part;
            this.currentDepth = currentDepth;
            this.isClosed = isClosed;
        }
    }

    StringBuilder getStringBuilder(){
        return sb;
    }

    /*=========================================================================*/
    /*--------------------      Parent Methods     ----------------------------*/
    /*=========================================================================*/

    /**
     * Void elements: area, base, br, col, embed, hr, img, input, link, meta, param, source, track, wbr.
     */
    private void visitParentSpecial(){
        if (!isCached || openDynamic){
            if (!isClosed){
                HtmlTags.appendOpenTagEnd(sb);

                if (!isCached){
                    HtmlTags.appendOpenTagEnd(cacheBitsStorage);
                }
            }
            else
                depth--;

            isClosed = true;
        }
    }

    @Override
    public final void visitParentHr() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentEmbed() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentInput() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentMeta() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentBr() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentCol() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentSource() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentImg() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentArea() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentLink() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentParam() {
        visitParentSpecial();
    }

    @Override
    public final void visitParentBase() {
        visitParentSpecial();
    }
}
