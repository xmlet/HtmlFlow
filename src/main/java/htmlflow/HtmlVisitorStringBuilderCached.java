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

import static htmlflow.HtmlTags.FINISH_TAG;

/**
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorStringBuilderCached extends ElementVisitor {

    private static final char NEWLINE = '\n';
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
            sb.append('\t');
            cacheBitsStorage.append('\t');
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
            HtmlTags.appendOpenComment(sb);
            sb.append(comment);
            HtmlTags.appendEndComment(sb);

            if (!isCached){
                HtmlTags.appendOpenComment(cacheBitsStorage);
                cacheBitsStorage.append(comment);
                HtmlTags.appendEndComment(cacheBitsStorage);
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

    private void visitParent2(String closingTag){
        if (!isCached || openDynamic){
            if (isClosed) {
                depth--;
            }

            tabs();
            HtmlTags.appendCloseTag2(sb, closingTag);

            if (!isCached){
                HtmlTags.appendCloseTag2(cacheBitsStorage, closingTag);
            }
        }
    }

    public final void visitParentSelect() {
        this.visitParent2("</select>");
    }

    public final void visitParentLegend() {
        this.visitParent2("</legend>");
    }

    public final void visitParentTextarea() {
        this.visitParent2("</textarea>");
    }

    public final void visitParentCaption() {
        this.visitParent2("</caption>");
    }

    public final void visitParentDel() {
        this.visitParent2("</del>");
    }

    public final void visitParentHr() {
        visitParentSpecial();
    }

    public final void visitParentOutput() {
        this.visitParent2("</output>");
    }

    public final void visitParentEmbed() {
        visitParentSpecial();
    }

    public final void visitParentAbbr() {
        this.visitParent2("</abbr>");
    }

    public final void visitParentNav() {
        this.visitParent2("</nav>");
    }

    public final void visitParentCanvas() {
        this.visitParent2("</canvas>");
    }

    public final void visitParentVar() {
        this.visitParent2("</var>");
    }

    public final void visitParentMathml() {
        this.visitParent2("</mathml>");
    }

    public final void visitParentDfn() {
        this.visitParent2("</dfn>");
    }

    public final void visitParentScript() {
        this.visitParent2("</script>");
    }

    public final void visitParentInput() {
        visitParentSpecial();
    }

    public final void visitParentMeta() {
        visitParentSpecial();
    }

    public final void visitParentStyle() {
        this.visitParent2("</style>");
    }

    public final void visitParentRp() {
        this.visitParent2("</rp>");
    }

    public final void visitParentObject() {
        this.visitParent2("</object>");
    }

    public final void visitParentSub() {
        this.visitParent2("</sub>");
    }

    public final void visitParentStrong() {
        this.visitParent2("</strong>");
    }

    public final void visitParentRt() {
        this.visitParent2("</rt>");
    }

    public final void visitParentSamp() {
        this.visitParent2("</samp>");
    }

    public final void visitParentHgroup() {
        this.visitParent2("</hgroup>");
    }

    public final void visitParentSup() {
        this.visitParent2("</sup>");
    }

    public final void visitParentBr() {
        visitParentSpecial();
    }

    public final void visitParentIframe() {
        this.visitParent2("</iframe>");
    }

    public final void visitParentAudio() {
        this.visitParent2("</audio>");
    }

    public final void visitParentMap() {
        this.visitParent2("</map>");
    }

    public final void visitParentTable() {
        this.visitParent2("</table>");
    }

    public final void visitParentA() {
        this.visitParent2("</a>");
    }

    public final void visitParentB() {
        this.visitParent2("</b>");
    }

    public final void visitParentAddress() {
        this.visitParent2("</address>");
    }

    public final void visitParentSvg() {
        this.visitParent2("</svg>");
    }

    public final void visitParentI() {
        this.visitParent2("</i>");
    }

    public final void visitParentBdo() {
        this.visitParent2("</bdo>");
    }

    public final void visitParentMenu() {
        this.visitParent2("</menu>");
    }

    public final void visitParentP() {
        this.visitParent2("</p>");
    }

    public final void visitParentTfoot() {
        this.visitParent2("</tfoot>");
    }

    public final void visitParentTd() {
        this.visitParent2("</td>");
    }

    public final void visitParentQ() {
        this.visitParent2("</q>");
    }

    public final void visitParentTh() {
        this.visitParent2("</th>");
    }

    public final void visitParentCite() {
        this.visitParent2("</cite>");
    }

    public final void visitParentProgress() {
        this.visitParent2("</progress>");
    }

    public final void visitParentLi() {
        this.visitParent2("</li>");
    }

    public final void visitParentTr() {
        this.visitParent2("</tr>");
    }

    public final void visitParentSpan() {
        this.visitParent2("</span>");
    }

    public final void visitParentDd() {
        this.visitParent2("</dd>");
    }

    public final void visitParentSmall() {
        this.visitParent2("</small>");
    }

    public final void visitParentCol() {
        visitParentSpecial();
    }

    public final void visitParentOptgroup() {
        this.visitParent2("</optgroup>");
    }

    public final void visitParentTbody() {
        this.visitParent2("</tbody>");
    }

    public final void visitParentDl() {
        this.visitParent2("</dl>");
    }

    public final void visitParentFieldset() {
        this.visitParent2("</fieldset>");
    }

    public final void visitParentSection() {
        this.visitParent2("</section>");
    }

    public final void visitParentSource() {
        visitParentSpecial();
    }

    public final void visitParentBody() {
        this.visitParent2("</body>");
    }

    public final void visitParentDt() {
        this.visitParent2("</dt>");
    }

    public final void visitParentDiv() {
        this.visitParent2("</div>");
    }

    public final void visitParentUl() {
        this.visitParent2("</ul>");
    }

    public final void visitParentHtml() {
        this.visitParent2("</html>");
    }

    public final void visitParentDetails() {
        this.visitParent2("</details>");
    }

    public final void visitParentArea() {
        visitParentSpecial();
    }

    public final void visitParentPre() {
        this.visitParent2("</pre>");
    }

    public final void visitParentBlockquote() {
        this.visitParent2("</blockquote>");
    }

    public final void visitParentMeter() {
        this.visitParent2("</meter>");
    }

    public final void visitParentEm() {
        this.visitParent2("</em>");
    }

    public final void visitParentArticle() {
        this.visitParent2("</article>");
    }

    public final void visitParentAside() {
        this.visitParent2("</aside>");
    }

    public final void visitParentNoscript() {
        this.visitParent2("</noscript>");
    }

    public final void visitParentHeader() {
        this.visitParent2("</header>");
    }

    public final void visitParentOption() {
        this.visitParent2("</option>");
    }

    public final void visitParentImg() {
        visitParentSpecial();
    }

    public final void visitParentCode() {
        this.visitParent2("</code>");
    }

    public final void visitParentFooter() {
        this.visitParent2("</footer>");
    }

    public final void visitParentThead() {
        this.visitParent2("</thead>");
    }

    public final void visitParentLink() {
        visitParentSpecial();
    }

    public final void visitParentH1() {
        this.visitParent2("</h1>");
    }

    public final void visitParentH2() {
        this.visitParent2("</h2>");
    }

    public final void visitParentH3() {
        this.visitParent2("</h3>");
    }

    public final void visitParentVideo() {
        this.visitParent2("</video>");
    }

    public final void visitParentTitle() {
        this.visitParent2("</title>");
    }

    public final void visitParentH4() {
        this.visitParent2("</h4>");
    }

    public final void visitParentH5() {
        this.visitParent2("</h5>");
    }

    public final void visitParentH6() {
        this.visitParent2("</h6>");
    }

    public final void visitParentKeygen() {
        this.visitParent2("</keygen>");
    }

    public final void visitParentHead() {
        this.visitParent2("</head>");
    }

    public final void visitParentButton() {
        this.visitParent2("</button>");
    }

    public final void visitParentDialog() {
        this.visitParent2("</dialog>");
    }

    public final void visitParentParam() {
        visitParentSpecial();
    }

    public final void visitParentOl() {
        this.visitParent2("</ol>");
    }

    public final void visitParentFigure() {
        this.visitParent2("</figure>");
    }

    public final void visitParentDatalist() {
        this.visitParent2("</datalist>");
    }

    public final void visitParentLabel() {
        this.visitParent2("</label>");
    }

    public final void visitParentColgroup() {
        this.visitParent2("</colgroup>");
    }

    public final void visitParentKbd() {
        this.visitParent2("</kbd>");
    }

    public final void visitParentCommand() {
        this.visitParent2("</command>");
    }

    public final void visitParentRuby() {
        this.visitParent2("</ruby>");
    }

    public final void visitParentIns() {
        this.visitParent2("</ins>");
    }

    public final void visitParentForm() {
        this.visitParent2("</form>");
    }

    public final void visitParentTime() {
        this.visitParent2("</time>");
    }

    public final void visitParentMark() {
        this.visitParent2("</mark>");
    }

    public final void visitParentBase() {
        visitParentSpecial();
    }

    /*=========================================================================*/
    /*--------------------     Element Methods     ----------------------------*/
    /*=========================================================================*/

    private void visitElement2(String openingTag) {
        if (!isCached || openDynamic){
            if (!isClosed){
                depth++;
            }

            tabs();
            HtmlTags.appendOpenTag2(sb, openingTag);

            if (!isCached){
                HtmlTags.appendOpenTag2(cacheBitsStorage, openingTag);
            }

            isClosed = false;
        }
    }

    public final void visitElementSelect() {
        this.visitElement2("<select");
    }

    public final void visitElementLegend() {
        this.visitElement2("<legend");
    }

    public final void visitElementTextarea() {
        this.visitElement2("<textarea");
    }

    public final void visitElementCaption() {
        this.visitElement2("<caption");
    }

    public final void visitElementDel() {
        this.visitElement2("<del");
    }

    public final void visitElementHr() {
        this.visitElement2("<hr");
    }

    public final void visitElementOutput() {
        this.visitElement2("<output");
    }

    public final void visitElementEmbed() {
        this.visitElement2("<embed");
    }

    public final void visitElementAbbr() {
        this.visitElement2("<abbr");
    }

    public final void visitElementNav() {
        this.visitElement2("<nav");
    }

    public final void visitElementCanvas() {
        this.visitElement2("<canvas");
    }

    public final void visitElementVar() {
        this.visitElement2("<var");
    }

    public final void visitElementMathml() {
        this.visitElement2("<mathml");
    }

    public final void visitElementDfn() {
        this.visitElement2("<dfn");
    }

    public final void visitElementScript() {
        this.visitElement2("<script");
    }

    public final void visitElementInput() {
        this.visitElement2("<input");
    }

    public final void visitElementMeta() {
        this.visitElement2("<meta");
    }

    public final void visitElementStyle() {
        this.visitElement2("<style");
    }

    public final void visitElementRp() {
        this.visitElement2("<rp");
    }

    public final void visitElementObject() {
        this.visitElement2("<object");
    }

    public final void visitElementSub() {
        this.visitElement2("<sub");
    }

    public final void visitElementStrong() {
        this.visitElement2("<strong");
    }

    public final void visitElementRt() {
        this.visitElement2("<rt");
    }

    public final void visitElementSamp() {
        this.visitElement2("<samp");
    }

    public final void visitElementHgroup() {
        this.visitElement2("<hgroup");
    }

    public final void visitElementSup() {
        this.visitElement2("<sup");
    }

    public final void visitElementBr() {
        this.visitElement2("<br");
    }

    public final void visitElementIframe() {
        this.visitElement2("<iframe");
    }

    public final void visitElementAudio() {
        this.visitElement2("<audio");
    }

    public final void visitElementMap() {
        this.visitElement2("<map");
    }

    public final void visitElementTable() {
        this.visitElement2("<table");
    }

    public final void visitElementA() {
        this.visitElement2("<a");
    }

    public final void visitElementB() {
        this.visitElement2("<b");
    }

    public final void visitElementAddress() {
        this.visitElement2("<address");
    }

    public final void visitElementSvg() {
        this.visitElement2("<svg");
    }

    public final void visitElementI() {
        this.visitElement2("<i");
    }

    public final void visitElementBdo() {
        this.visitElement2("<bdo");
    }

    public final void visitElementMenu() {
        this.visitElement2("<menu");
    }

    public final void visitElementP() {
        this.visitElement2("<p");
    }

    public final void visitElementTfoot() {
        this.visitElement2("<tfoot");
    }

    public final void visitElementTd() {
        this.visitElement2("<td");
    }

    public final void visitElementQ() {
        this.visitElement2("<q");
    }

    public final void visitElementTh() {
        this.visitElement2("<th");
    }

    public final void visitElementCite() {
        this.visitElement2("<cite");
    }

    public final void visitElementProgress() {
        this.visitElement2("<progress");
    }

    public final void visitElementLi() {
        this.visitElement2("<li");
    }

    public final void visitElementTr() {
        this.visitElement2("<tr");
    }

    public final void visitElementSpan() {
        this.visitElement2("<span");
    }

    public final void visitElementDd() {
        this.visitElement2("<dd");
    }

    public final void visitElementSmall() {
        this.visitElement2("<small");
    }

    public final void visitElementCol() {
        this.visitElement2("<col");
    }

    public final void visitElementOptgroup() {
        this.visitElement2("<optgroup");
    }

    public final void visitElementTbody() {
        this.visitElement2("<tbody");
    }

    public final void visitElementDl() {
        this.visitElement2("<dl");
    }

    public final void visitElementFieldset() {
        this.visitElement2("<fieldset");
    }

    public final void visitElementSection() {
        this.visitElement2("<section");
    }

    public final void visitElementSource() {
        this.visitElement2("<source");
    }

    public final void visitElementBody() {
        this.visitElement2("<body");
    }

    public final void visitElementDt() {
        this.visitElement2("<dt");
    }

    public final void visitElementDiv() {
        this.visitElement2("<div");
    }

    public final void visitElementUl() {
        this.visitElement2("<ul");
    }

    public final void visitElementHtml() {
        this.visitElement2("<html");
    }

    public final void visitElementDetails() {
        this.visitElement2("<details");
    }

    public final void visitElementArea() {
        this.visitElement2("<area");
    }

    public final void visitElementPre() {
        this.visitElement2("<pre");
    }

    public final void visitElementBlockquote() {
        this.visitElement2("<blockquote");
    }

    public final void visitElementMeter() {
        this.visitElement2("<meter");
    }

    public final void visitElementEm() {
        this.visitElement2("<em");
    }

    public final void visitElementArticle() {
        this.visitElement2("<article");
    }

    public final void visitElementAside() {
        this.visitElement2("<aside");
    }

    public final void visitElementNoscript() {
        this.visitElement2("<noscript");
    }

    public final void visitElementHeader() {
        this.visitElement2("<header");
    }

    public final void visitElementOption() {
        this.visitElement2("<option");
    }

    public final void visitElementImg() {
        this.visitElement2("<img");
    }

    public final void visitElementCode() {
        this.visitElement2("<code");
    }

    public final void visitElementFooter() {
        this.visitElement2("<footer");
    }

    public final void visitElementThead() {
        this.visitElement2("<thead");
    }

    public final void visitElementLink() {
        this.visitElement2("<link");
    }

    public final void visitElementH1() {
        this.visitElement2("<h1");
    }

    public final void visitElementH2() {
        this.visitElement2("<h2");
    }

    public final void visitElementH3() {
        this.visitElement2("<h3");
    }

    public final void visitElementVideo() {
        this.visitElement2("<video");
    }

    public final void visitElementTitle() {
        this.visitElement2("<title");
    }

    public final void visitElementH4() {
        this.visitElement2("<h4");
    }

    public final void visitElementH5() {
        this.visitElement2("<h5");
    }

    public final void visitElementH6() {
        this.visitElement2("<h6");
    }

    public final void visitElementKeygen() {
        this.visitElement2("<keygen");
    }

    public final void visitElementHead() {
        this.visitElement2("<head");
    }

    public final void visitElementButton() {
        this.visitElement2("<button");
    }

    public final void visitElementDialog() {
        this.visitElement2("<dialog");
    }

    public final void visitElementParam() {
        this.visitElement2("<param");
    }

    public final void visitElementOl() {
        this.visitElement2("<ol");
    }

    public final void visitElementFigure() {
        this.visitElement2("<figure");
    }

    public final void visitElementDatalist() {
        this.visitElement2("<datalist");
    }

    public final void visitElementLabel() {
        this.visitElement2("<label");
    }

    public final void visitElementColgroup() {
        this.visitElement2("<colgroup");
    }

    public final void visitElementKbd() {
        this.visitElement2("<kbd");
    }

    public final void visitElementCommand() {
        this.visitElement2("<command");
    }

    public final void visitElementRuby() {
        this.visitElement2("<ruby");
    }

    public final void visitElementIns() {
        this.visitElement2("<ins");
    }

    public final void visitElementForm() {
        this.visitElement2("<form");
    }

    public final void visitElementTime() {
        this.visitElement2("<time");
    }

    public final void visitElementMark() {
        this.visitElement2("<mark");
    }

    public final void visitElementBase() {
        this.visitElement2("<base");
    }

    /*=========================================================================*/
    /*--------------------    Attribute Methods    ----------------------------*/
    /*=========================================================================*/

    private void visitAttribute2(String name, String attributeValue){
        if (!isCached || openDynamic) {
            HtmlTags.appendAttribute2(sb, name, attributeValue);

            if (!isCached) {
                HtmlTags.appendAttribute2(cacheBitsStorage, name, attributeValue);
            }
        }
    }

    public final void visitAttributeOnfocus(String attributeValue) {
        visitAttribute2(" onfocus=\"", attributeValue);
    }

    public final void visitAttributeOninvalid(String attributeValue) {
        visitAttribute2(" oninvalid=\"", attributeValue);
    }

    public final void visitAttributeType(String attributeValue) {
        visitAttribute2(" type=\"", attributeValue);
    }

    public final void visitAttributeOnratechange(String attributeValue) {
        visitAttribute2(" onratechange=\"", attributeValue);
    }

    public final void visitAttributeRequired(String attributeValue) {
        visitAttribute2(" required=\"", attributeValue);
    }

    public final void visitAttributeIsmap(String attributeValue) {
        visitAttribute2(" ismap=\"", attributeValue);
    }

    public final void visitAttributeOnmessage(String attributeValue) {
        visitAttribute2(" onmessage=\"", attributeValue);
    }

    public final void visitAttributeOncanplaythrough(String attributeValue) {
        visitAttribute2(" oncanplaythrough=\"", attributeValue);
    }

    public final void visitAttributeSizes(String attributeValue) {
        visitAttribute2(" sizes=\"", attributeValue);
    }

    public final void visitAttributeReadonly(String attributeValue) {
        visitAttribute2(" readonly=\"", attributeValue);
    }

    public final void visitAttributeOnselect(String attributeValue) {
        visitAttribute2(" onselect=\"", attributeValue);
    }

    public final void visitAttributeAction(String attributeValue) {
        visitAttribute2(" action=\"", attributeValue);
    }

    public final void visitAttributeOnscroll(String attributeValue) {
        visitAttribute2(" onscroll=\"", attributeValue);
    }

    public final void visitAttributeHref(String attributeValue) {
        visitAttribute2(" href=\"", attributeValue);
    }

    public final void visitAttributeId(String attributeValue) {
        visitAttribute2(" id=\"", attributeValue);
    }

    public final void visitAttributeEnableViewState(String attributeValue) {
        visitAttribute2(" EnableViewState=\"", attributeValue);
    }

    public final void visitAttributeOnkeydown(String attributeValue) {
        visitAttribute2(" onkeydown=\"", attributeValue);
    }

    public final void visitAttributeHeight(String attributeValue) {
        visitAttribute2(" height=\"", attributeValue);
    }

    public final void visitAttributeEnctype(String attributeValue) {
        visitAttribute2(" enctype=\"", attributeValue);
    }

    public final void visitAttributeSeamless(String attributeValue) {
        visitAttribute2(" seamless=\"", attributeValue);
    }

    public final void visitAttributeItem(String attributeValue) {
        visitAttribute2(" item=\"", attributeValue);
    }

    public final void visitAttributeMethod(String attributeValue) {
        visitAttribute2(" method=\"", attributeValue);
    }

    public final void visitAttributeMaxlength(String attributeValue) {
        visitAttribute2(" maxlength=\"", attributeValue);
    }

    public final void visitAttributePubdate(String attributeValue) {
        visitAttribute2(" Pubdate=\"", attributeValue);
    }

    public final void visitAttributeOnclick(String attributeValue) {
        visitAttribute2(" onclick=\"", attributeValue);
    }

    public final void visitAttributeOnkeyup(String attributeValue) {
        visitAttribute2(" onkeyup=\"", attributeValue);
    }

    public final void visitAttributeOnchange(String attributeValue) {
        visitAttribute2(" onchange=\"", attributeValue);
    }

    public final void visitAttributeAccept(String attributeValue) {
        visitAttribute2(" accept=\"", attributeValue);
    }

    public final void visitAttributeOnkeypress(String attributeValue) {
        visitAttribute2(" onkeypress=\"", attributeValue);
    }

    public final void visitAttributeOndblclick(String attributeValue) {
        visitAttribute2(" ondblclick=\"", attributeValue);
    }

    public final void visitAttributeSize(String attributeValue) {
        visitAttribute2(" size=\"", attributeValue);
    }

    public final void visitAttributeOndrag(String attributeValue) {
        visitAttribute2(" ondrag=\"", attributeValue);
    }

    public final void visitAttributeRadiogroup(String attributeValue) {
        visitAttribute2(" radiogroup=\"", attributeValue);
    }

    public final void visitAttributeSpellcheck(String attributeValue) {
        visitAttribute2(" spellcheck=\"", attributeValue);
    }

    public final void visitAttributeOnbeforeunload(String attributeValue) {
        visitAttribute2(" onbeforeunload=\"", attributeValue);
    }

    public final void visitAttributeStyle(String attributeValue) {
        visitAttribute2(" style=\"", attributeValue);
    }

    public final void visitAttributeOnseeked(String attributeValue) {
        visitAttribute2(" onseeked=\"", attributeValue);
    }

    public final void visitAttributeOnoffline(String attributeValue) {
        visitAttribute2(" onoffline=\"", attributeValue);
    }

    public final void visitAttributePoster(String attributeValue) {
        visitAttribute2(" poster=\"", attributeValue);
    }

    public final void visitAttributeOnplay(String attributeValue) {
        visitAttribute2(" onplay=\"", attributeValue);
    }

    public final void visitAttributeCharset(String attributeValue) {
        visitAttribute2(" charset=\"", attributeValue);
    }

    public final void visitAttributeOncontextmenu(String attributeValue) {
        visitAttribute2(" oncontextmenu=\"", attributeValue);
    }

    public final void visitAttributeOnmousemove(String attributeValue) {
        visitAttribute2(" onmousemove=\"", attributeValue);
    }

    public final void visitAttributeShape(String attributeValue) {
        visitAttribute2(" Shape=\"", attributeValue);
    }

    public final void visitAttributeRole(String attributeValue) {
        visitAttribute2(" role=\"", attributeValue);
    }

    public final void visitAttributeOnreadystatechange(String attributeValue) {
        visitAttribute2(" onreadystatechange=\"", attributeValue);
    }

    public final void visitAttributePing(String attributeValue) {
        visitAttribute2(" ping=\"", attributeValue);
    }

    public final void visitAttributeOnUnloadAlt(String attributeValue) {
        visitAttribute2(" OnUnloadAlt=\"", attributeValue);
    }

    public final void visitAttributeIcon(String attributeValue) {
        visitAttribute2(" icon=\"", attributeValue);
    }

    public final void visitAttributePattern(String attributeValue) {
        visitAttribute2(" pattern=\"", attributeValue);
    }

    public final void visitAttributeOnmouseover(String attributeValue) {
        visitAttribute2(" onmouseover=\"", attributeValue);
    }

    public final void visitAttributeColspan(String attributeValue) {
        visitAttribute2(" colspan=\"", attributeValue);
    }

    public final void visitAttributeOnInit(String attributeValue) {
        visitAttribute2(" OnInit=\"", attributeValue);
    }

    public final void visitAttributeDataFolderName(String attributeValue) {
        visitAttribute2(" data-FolderName=\"", attributeValue);
    }

    public final void visitAttributeMin(String attributeValue) {
        visitAttribute2(" min=\"", attributeValue);
    }

    public final void visitAttributeNovalidate(String attributeValue) {
        visitAttribute2(" novalidate=\"", attributeValue);
    }

    public final void visitAttributeFormenctype(String attributeValue) {
        visitAttribute2(" formenctype=\"", attributeValue);
    }

    public final void visitAttributeDraggable(String attributeValue) {
        visitAttribute2(" draggable=\"", attributeValue);
    }

    public final void visitAttributeLow(String attributeValue) {
        visitAttribute2(" low=\"", attributeValue);
    }

    public final void visitAttributeVisible(String attributeValue) {
        visitAttribute2(" Visible=\"", attributeValue);
    }

    public final void visitAttributeOnafterprint(String attributeValue) {
        visitAttribute2(" onafterprint=\"", attributeValue);
    }

    public final void visitAttributeOndragenter(String attributeValue) {
        visitAttribute2(" ondragenter=\"", attributeValue);
    }

    public final void visitAttributeOnloadAlt(String attributeValue) {
        visitAttribute2(" onloadAlt=\"", attributeValue);
    }

    public final void visitAttributeDisabled(String attributeValue) {
        visitAttribute2(" disabled=\"", attributeValue);
    }

    public final void visitAttributeUsemap(String attributeValue) {
        visitAttribute2(" usemap=\"", attributeValue);
    }

    public final void visitAttributeCoords(String attributeValue) {
        visitAttribute2(" coords=\"", attributeValue);
    }

    public final void visitAttributeOnmousewheel(String attributeValue) {
        visitAttribute2(" onmousewheel=\"", attributeValue);
    }

    public final void visitAttributeOnseeking(String attributeValue) {
        visitAttribute2(" onseeking=\"", attributeValue);
    }

    public final void visitAttributeOnblur(String attributeValue) {
        visitAttribute2(" onblur=\"", attributeValue);
    }

    public final void visitAttributeOnvolumenchange(String attributeValue) {
        visitAttribute2(" onvolumenchange=\"", attributeValue);
    }

    public final void visitAttributeMax(String attributeValue) {
        visitAttribute2(" max=\"", attributeValue);
    }

    public final void visitAttributeContextmenu(String attributeValue) {
        visitAttribute2(" contextmenu=\"", attributeValue);
    }

    public final void visitAttributeOptimum(String attributeValue) {
        visitAttribute2(" optimum=\"", attributeValue);
    }

    public final void visitAttributeKeytype(String attributeValue) {
        visitAttribute2(" keytype=\"", attributeValue);
    }

    public final void visitAttributeOndurationchange(String attributeValue) {
        visitAttribute2(" ondurationchange=\"", attributeValue);
    }

    public final void visitAttributeOnplaying(String attributeValue) {
        visitAttribute2(" onplaying=\"", attributeValue);
    }

    public final void visitAttributeOnended(String attributeValue) {
        visitAttribute2(" onended=\"", attributeValue);
    }

    public final void visitAttributeOnloadeddata(String attributeValue) {
        visitAttribute2(" onloadeddata=\"", attributeValue);
    }

    public final void visitAttributeClassid(String attributeValue) {
        visitAttribute2(" classid=\"", attributeValue);
    }

    public final void visitAttributeScoped(String attributeValue) {
        visitAttribute2(" scoped=\"", attributeValue);
    }

    public final void visitAttributeAcceptCharset(String attributeValue) {
        visitAttribute2(" accept-charset=\"", attributeValue);
    }

    public final void visitAttributeValidationGroup(String attributeValue) {
        visitAttribute2(" ValidationGroup=\"", attributeValue);
    }

    public final void visitAttributeOnmouseout(String attributeValue) {
        visitAttribute2(" onmouseout=\"", attributeValue);
    }

    public final void visitAttributeOnsuspend(String attributeValue) {
        visitAttribute2(" onsuspend=\"", attributeValue);
    }

    public final void visitAttributeWidth(String attributeValue) {
        visitAttribute2(" width=\"", attributeValue);
    }

    public final void visitAttributeOnwaiting(String attributeValue) {
        visitAttribute2(" onwaiting=\"", attributeValue);
    }

    public final void visitAttributeCite(String attributeValue) {
        visitAttribute2(" cite=\"", attributeValue);
    }

    public final void visitAttributeOncanplay(String attributeValue) {
        visitAttribute2(" oncanplay=\"", attributeValue);
    }

    public final void visitAttributeOnmousedown(String attributeValue) {
        visitAttribute2(" onmousedown=\"", attributeValue);
    }

    public final void visitAttributeAutobuffer(String attributeValue) {
        visitAttribute2(" autobuffer=\"", attributeValue);
    }

    public final void visitAttributeWrap(String attributeValue) {
        visitAttribute2(" wrap=\"", attributeValue);
    }

    public final void visitAttributeOpen(String attributeValue) {
        visitAttribute2(" open=\"", attributeValue);
    }

    public final void visitAttributeOnemptied(String attributeValue) {
        visitAttribute2(" onemptied=\"", attributeValue);
    }

    public final void visitAttributeSpan(String attributeValue) {
        visitAttribute2(" span=\"", attributeValue);
    }

    public final void visitAttributeControls(String attributeValue) {
        visitAttribute2(" controls=\"", attributeValue);
    }

    public final void visitAttributeRunat(String attributeValue) {
        visitAttribute2(" runat=\"", attributeValue);
    }

    public final void visitAttributeData(String attributeValue) {
        visitAttribute2(" data=\"", attributeValue);
    }

    public final void visitAttributeOnLoad(String attributeValue) {
        visitAttribute2(" OnLoad=\"", attributeValue);
    }

    public final void visitAttributeSubject(String attributeValue) {
        visitAttribute2(" subject=\"", attributeValue);
    }

    public final void visitAttributeTabIndex(String attributeValue) {
        visitAttribute2(" tabIndex=\"", attributeValue);
    }

    public final void visitAttributeDir(String attributeValue) {
        visitAttribute2(" dir=\"", attributeValue);
    }

    public final void visitAttributeOnDataBinding(String attributeValue) {
        visitAttribute2(" OnDataBinding=\"", attributeValue);
    }

    public final void visitAttributeOnresize(String attributeValue) {
        visitAttribute2(" onresize=\"", attributeValue);
    }

    public final void visitAttributeFormaction(String attributeValue) {
        visitAttribute2(" formaction=\"", attributeValue);
    }

    public final void visitAttributeHigh(String attributeValue) {
        visitAttribute2(" high=\"", attributeValue);
    }

    public final void visitAttributeDatetime(String attributeValue) {
        visitAttribute2(" datetime=\"", attributeValue);
    }

    public final void visitAttributeAccesskey(String attributeValue) {
        visitAttribute2(" accesskey=\"", attributeValue);
    }

    public final void visitAttributeOnPreRender(String attributeValue) {
        visitAttribute2(" OnPreRender=\"", attributeValue);
    }

    public final void visitAttributeLoop(String attributeValue) {
        visitAttribute2(" loop=\"", attributeValue);
    }

    public final void visitAttributeRowspan(String attributeValue) {
        visitAttribute2(" rowspan=\"", attributeValue);
    }

    public final void visitAttributeOnpause(String attributeValue) {
        visitAttribute2(" onpause=\"", attributeValue);
    }

    public final void visitAttributeRel(String attributeValue) {
        visitAttribute2(" rel=\"", attributeValue);
    }

    public final void visitAttributeOnloadstart(String attributeValue) {
        visitAttribute2(" onloadstart=\"", attributeValue);
    }

    public final void visitAttributeOnprogress(String attributeValue) {
        visitAttribute2(" onprogress=\"", attributeValue);
    }

    public final void visitAttributeChecked(String attributeValue) {
        visitAttribute2(" checked=\"", attributeValue);
    }

    public final void visitAttributeOnbeforeprint(String attributeValue) {
        visitAttribute2(" onbeforeprint=\"", attributeValue);
    }

    public final void visitAttributeSelected(String attributeValue) {
        visitAttribute2(" selected=\"", attributeValue);
    }

    public final void visitAttributeOnstorage(String attributeValue) {
        visitAttribute2(" onstorage=\"", attributeValue);
    }

    public final void visitAttributeFormmethod(String attributeValue) {
        visitAttribute2(" formmethod=\"", attributeValue);
    }

    public final void visitAttributeOnforminput(String attributeValue) {
        visitAttribute2(" onforminput=\"", attributeValue);
    }

    public final void visitAttributeAlt(String attributeValue) {
        visitAttribute2(" alt=\"", attributeValue);
    }

    public final void visitAttributeList(String attributeValue) {
        visitAttribute2(" list=\"", attributeValue);
    }

    public final void visitAttributeAutoplay(String attributeValue) {
        visitAttribute2(" autoplay=\"", attributeValue);
    }

    public final void visitAttributeOnDisposed(String attributeValue) {
        visitAttribute2(" OnDisposed=\"", attributeValue);
    }

    public final void visitAttributeOntimeupdate(String attributeValue) {
        visitAttribute2(" ontimeupdate=\"", attributeValue);
    }

    public final void visitAttributeHreflang(String attributeValue) {
        visitAttribute2(" hreflang=\"", attributeValue);
    }

    public final void visitAttributeFormnovalidate(String attributeValue) {
        visitAttribute2(" formnovalidate=\"", attributeValue);
    }

    public final void visitAttributeName(String attributeValue) {
        visitAttribute2(" name=\"", attributeValue);
    }

    public final void visitAttributeFiles(String attributeValue) {
        visitAttribute2(" files=\"", attributeValue);
    }

    public final void visitAttributeOnabort(String attributeValue) {
        visitAttribute2(" onabort=\"", attributeValue);
    }

    public final void visitAttributeOnloadedmetadata(String attributeValue) {
        visitAttribute2(" onloadedmetadata=\"", attributeValue);
    }

    public final void visitAttributeDefer(String attributeValue) {
        visitAttribute2(" defer=\"", attributeValue);
    }

    public final void visitAttributeContenteditable(String attributeValue) {
        visitAttribute2(" contenteditable=\"", attributeValue);
    }

    public final void visitAttributeHttpEquiv(String attributeValue) {
        visitAttribute2(" http-equiv=\"", attributeValue);
    }

    public final void visitAttributeHidden(String attributeValue) {
        visitAttribute2(" hidden=\"", attributeValue);
    }

    public final void visitAttributeOnerror(String attributeValue) {
        visitAttribute2(" onerror=\"", attributeValue);
    }

    public final void visitAttributeOnmouseup(String attributeValue) {
        visitAttribute2(" onmouseup=\"", attributeValue);
    }

    public final void visitAttributeEnableTheming(String attributeValue) {
        visitAttribute2(" EnableTheming=\"", attributeValue);
    }

    public final void visitAttributeOndragover(String attributeValue) {
        visitAttribute2(" ondragover=\"", attributeValue);
    }

    public final void visitAttributeOnhashchange(String attributeValue) {
        visitAttribute2(" onhashchange=\"", attributeValue);
    }

    public final void visitAttributeOnonline(String attributeValue) {
        visitAttribute2(" ononline=\"", attributeValue);
    }

    public final void visitAttributeFor(String attributeValue) {
        visitAttribute2(" for=\"", attributeValue);
    }

    public final void visitAttributeItemprop(String attributeValue) {
        visitAttribute2(" itemprop=\"", attributeValue);
    }

    public final void visitAttributeMedia(String attributeValue) {
        visitAttribute2(" media=\"", attributeValue);
    }

    public final void visitAttributeTitle(String attributeValue) {
        visitAttribute2(" title=\"", attributeValue);
    }

    public final void visitAttributeOnformchange(String attributeValue) {
        visitAttribute2(" onformchange=\"", attributeValue);
    }

    public final void visitAttributeContent(String attributeValue) {
        visitAttribute2(" content=\"", attributeValue);
    }

    public final void visitAttributeSkinID(String attributeValue) {
        visitAttribute2(" SkinID=\"", attributeValue);
    }

    public final void visitAttributeOninput(String attributeValue) {
        visitAttribute2(" oninput=\"", attributeValue);
    }

    public final void visitAttributeOnstalled(String attributeValue) {
        visitAttribute2(" onstalled=\"", attributeValue);
    }

    public final void visitAttributeScope(String attributeValue) {
        visitAttribute2(" scope=\"", attributeValue);
    }

    public final void visitAttributePlaceholder(String attributeValue) {
        visitAttribute2(" placeholder=\"", attributeValue);
    }

    public final void visitAttributeOnsubmit(String attributeValue) {
        visitAttribute2(" onsubmit=\"", attributeValue);
    }

    public final void visitAttributeLang(String attributeValue) {
        visitAttribute2(" lang=\"", attributeValue);
    }

    public final void visitAttributeValue(String attributeValue) {
        visitAttribute2(" value=\"", attributeValue);
    }

    public final void visitAttributeCols(String attributeValue) {
        visitAttribute2(" cols=\"", attributeValue);
    }

    public final void visitAttributeClass(String attributeValue) {
        visitAttribute2(" class=\"", attributeValue);
    }

    public final void visitAttributeOndragleave(String attributeValue) {
        visitAttribute2(" ondragleave=\"", attributeValue);
    }

    public final void visitAttributeSummary(String attributeValue) {
        visitAttribute2(" summary=\"", attributeValue);
    }

    public final void visitAttributeHeaders(String attributeValue) {
        visitAttribute2(" headers=\"", attributeValue);
    }

    public final void visitAttributeSrc(String attributeValue) {
        visitAttribute2(" src=\"", attributeValue);
    }

    public final void visitAttributeAutocomplete(String attributeValue) {
        visitAttribute2(" autocomplete=\"", attributeValue);
    }

    public final void visitAttributeManifest(String attributeValue) {
        visitAttribute2(" manifest=\"", attributeValue);
    }

    public final void visitAttributeStart(String attributeValue) {
        visitAttribute2(" start=\"", attributeValue);
    }

    public final void visitAttributeSandbox(String attributeValue) {
        visitAttribute2(" sandbox=\"", attributeValue);
    }

    public final void visitAttributeMultiple(String attributeValue) {
        visitAttribute2(" multiple=\"", attributeValue);
    }

    public final void visitAttributeOnunload(String attributeValue) {
        visitAttribute2(" onunload=\"", attributeValue);
    }

    public final void visitAttributeOndrop(String attributeValue) {
        visitAttribute2(" ondrop=\"", attributeValue);
    }

    public final void visitAttributeLabel(String attributeValue) {
        visitAttribute2(" label=\"", attributeValue);
    }

    public final void visitAttributeAutofocus(String attributeValue) {
        visitAttribute2(" autofocus=\"", attributeValue);
    }

    public final void visitAttributeRows(String attributeValue) {
        visitAttribute2(" rows=\"", attributeValue);
    }

    public final void visitAttributeOndragstart(String attributeValue) {
        visitAttribute2(" ondragstart=\"", attributeValue);
    }

    public final void visitAttributeOnundo(String attributeValue) {
        visitAttribute2(" onundo=\"", attributeValue);
    }

    public final void visitAttributeOnServerClick(String attributeValue) {
        visitAttribute2(" OnServerClick=\"", attributeValue);
    }

    public final void visitAttributeTarget(String attributeValue) {
        visitAttribute2(" target=\"", attributeValue);
    }

    public final void visitAttributeFormtarget(String attributeValue) {
        visitAttribute2(" formtarget=\"", attributeValue);
    }

    public final void visitAttributeOnredo(String attributeValue) {
        visitAttribute2(" onredo=\"", attributeValue);
    }

    public final void visitAttributeAsync(String attributeValue) {
        visitAttribute2(" async=\"", attributeValue);
    }

    public final void visitAttributeOnshow(String attributeValue) {
        visitAttribute2(" onshow=\"", attributeValue);
    }

    public final void visitAttributeForm(String attributeValue) {
        visitAttribute2(" form=\"", attributeValue);
    }

    public final void visitAttributeDataMsgId(String attributeValue) {
        visitAttribute2(" data-MsgId=\"", attributeValue);
    }

    public final void visitAttributeCausesValidation(String attributeValue) {
        visitAttribute2(" CausesValidation=\"", attributeValue);
    }

    public final void visitAttributeChallenge(String attributeValue) {
        visitAttribute2(" challenge=\"", attributeValue);
    }

    public final void visitAttributeStep(String attributeValue) {
        visitAttribute2(" step=\"", attributeValue);
    }

    public final void visitAttributeReversed(String attributeValue) {
        visitAttribute2(" reversed=\"", attributeValue);
    }

    public final void visitAttributeOnpopstate(String attributeValue) {
        visitAttribute2(" onpopstate=\"", attributeValue);
    }
}
