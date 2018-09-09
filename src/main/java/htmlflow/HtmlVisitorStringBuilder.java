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

/**
 * @author Miguel Gamboa
 *         created on 17-01-2018
 */
public class HtmlVisitorStringBuilder extends ElementVisitor {

    private static final int tabsMax;
    private static String[] tabs;
    private static String[] closedTabs;

    static {
        tabsMax = 1000;
        tabs = HtmlUtils.createTabs(tabsMax);
        closedTabs = HtmlUtils.createClosedTabs(tabsMax);
    }

    private final StringBuilder sb;
    private int depth;
    private boolean isClosed = true;

    HtmlVisitorStringBuilder(StringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public final void visitElement(String elementName) {
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.appendOpenTag(sb, elementName);
        isClosed = false;
    }

    @Override
    public final void visitParent(String elementName) {
        if (isClosed){
            depth--;
        }

        tabs();
        HtmlTags.appendCloseTag(sb, elementName);
    }

    @Override
    public final void visitAttribute(String attributeName, String attributeValue) {
        HtmlTags.appendAttribute(sb, attributeName, attributeValue);
    }

    /**
     * An optimized version of Text without binder.
     */
    @Override
    public final <R> void visitText(R text) {
        if (!isClosed){
            depth++;
        }

        tabs();
        sb.append(text);
    }

    @Override
    public final <R> void visitComment(R comment) {
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.appendOpenComment(sb);
        sb.append(comment);
        HtmlTags.appendEndComment(sb);
    }

    /*=========================================================================*/
    /*--------------------    Auxiliary Methods    ----------------------------*/
    /*=========================================================================*/

    private void tabs(){
        if (isClosed){
            sb.append(tabs[depth]);
        } else {
            sb.append(closedTabs[depth]);
            isClosed = true;
        }
    }

    /*=========================================================================*/
    /*--------------------      Parent Methods     ----------------------------*/
    /*=========================================================================*/
    
    private void visitParentSpecial(){
        if (!isClosed)
            HtmlTags.appendOpenTagEnd(sb);
        else
            depth--;
        isClosed = true;
    }

    private void visitParent2(String closingTag){
        if (isClosed){
            depth--;
        }

        tabs();
        HtmlTags.appendCloseTag2(sb, closingTag);
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
        if (!isClosed){
            depth++;
        }

        tabs();
        HtmlTags.appendOpenTag2(sb, openingTag);
        isClosed = false;
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

    public final void visitAttributeOnfocus(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onfocus=\"", attributeValue);
    }

    public final void visitAttributeOninvalid(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " oninvalid=\"", attributeValue);
    }

    public final void visitAttributeType(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " type=\"", attributeValue);
    }

    public final void visitAttributeOnratechange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onratechange=\"", attributeValue);
    }

    public final void visitAttributeRequired(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " required=\"", attributeValue);
    }

    public final void visitAttributeIsmap(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ismap=\"", attributeValue);
    }

    public final void visitAttributeOnmessage(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmessage=\"", attributeValue);
    }

    public final void visitAttributeOncanplaythrough(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " oncanplaythrough=\"", attributeValue);
    }

    public final void visitAttributeSizes(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " sizes=\"", attributeValue);
    }

    public final void visitAttributeReadonly(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " readonly=\"", attributeValue);
    }

    public final void visitAttributeOnselect(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onselect=\"", attributeValue);
    }

    public final void visitAttributeAction(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " action=\"", attributeValue);
    }

    public final void visitAttributeOnscroll(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onscroll=\"", attributeValue);
    }

    public final void visitAttributeHref(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " href=\"", attributeValue);
    }

    public final void visitAttributeId(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " id=\"", attributeValue);
    }

    public final void visitAttributeEnableViewState(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " EnableViewState=\"", attributeValue);
    }

    public final void visitAttributeOnkeydown(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onkeydown=\"", attributeValue);
    }

    public final void visitAttributeHeight(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " height=\"", attributeValue);
    }

    public final void visitAttributeEnctype(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " enctype=\"", attributeValue);
    }

    public final void visitAttributeSeamless(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " seamless=\"", attributeValue);
    }

    public final void visitAttributeItem(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " item=\"", attributeValue);
    }

    public final void visitAttributeMethod(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " method=\"", attributeValue);
    }

    public final void visitAttributeMaxlength(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " maxlength=\"", attributeValue);
    }

    public final void visitAttributePubdate(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " Pubdate=\"", attributeValue);
    }

    public final void visitAttributeOnclick(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onclick=\"", attributeValue);
    }

    public final void visitAttributeOnkeyup(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onkeyup=\"", attributeValue);
    }

    public final void visitAttributeOnchange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onchange=\"", attributeValue);
    }

    public final void visitAttributeAccept(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " accept=\"", attributeValue);
    }

    public final void visitAttributeOnkeypress(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onkeypress=\"", attributeValue);
    }

    public final void visitAttributeOndblclick(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondblclick=\"", attributeValue);
    }

    public final void visitAttributeSize(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " size=\"", attributeValue);
    }

    public final void visitAttributeOndrag(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondrag=\"", attributeValue);
    }

    public final void visitAttributeRadiogroup(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " radiogroup=\"", attributeValue);
    }

    public final void visitAttributeSpellcheck(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " spellcheck=\"", attributeValue);
    }

    public final void visitAttributeOnbeforeunload(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onbeforeunload=\"", attributeValue);
    }

    public final void visitAttributeStyle(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " style=\"", attributeValue);
    }

    public final void visitAttributeOnseeked(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onseeked=\"", attributeValue);
    }

    public final void visitAttributeOnoffline(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onoffline=\"", attributeValue);
    }

    public final void visitAttributePoster(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " poster=\"", attributeValue);
    }

    public final void visitAttributeOnplay(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onplay=\"", attributeValue);
    }

    public final void visitAttributeCharset(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " charset=\"", attributeValue);
    }

    public final void visitAttributeOncontextmenu(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " oncontextmenu=\"", attributeValue);
    }

    public final void visitAttributeOnmousemove(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmousemove=\"", attributeValue);
    }

    public final void visitAttributeShape(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " Shape=\"", attributeValue);
    }

    public final void visitAttributeRole(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " role=\"", attributeValue);
    }

    public final void visitAttributeOnreadystatechange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onreadystatechange=\"", attributeValue);
    }

    public final void visitAttributePing(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ping=\"", attributeValue);
    }

    public final void visitAttributeOnUnloadAlt(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnUnloadAlt=\"", attributeValue);
    }

    public final void visitAttributeIcon(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " icon=\"", attributeValue);
    }

    public final void visitAttributePattern(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " pattern=\"", attributeValue);
    }

    public final void visitAttributeOnmouseover(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmouseover=\"", attributeValue);
    }

    public final void visitAttributeColspan(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " colspan=\"", attributeValue);
    }

    public final void visitAttributeOnInit(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnInit=\"", attributeValue);
    }

    public final void visitAttributeDataFolderName(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " data-FolderName=\"", attributeValue);
    }

    public final void visitAttributeMin(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " min=\"", attributeValue);
    }

    public final void visitAttributeNovalidate(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " novalidate=\"", attributeValue);
    }

    public final void visitAttributeFormenctype(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " formenctype=\"", attributeValue);
    }

    public final void visitAttributeDraggable(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " draggable=\"", attributeValue);
    }

    public final void visitAttributeLow(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " low=\"", attributeValue);
    }

    public final void visitAttributeVisible(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " Visible=\"", attributeValue);
    }

    public final void visitAttributeOnafterprint(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onafterprint=\"", attributeValue);
    }

    public final void visitAttributeOndragenter(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondragenter=\"", attributeValue);
    }

    public final void visitAttributeOnloadAlt(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onloadAlt=\"", attributeValue);
    }

    public final void visitAttributeDisabled(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " disabled=\"", attributeValue);
    }

    public final void visitAttributeUsemap(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " usemap=\"", attributeValue);
    }

    public final void visitAttributeCoords(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " coords=\"", attributeValue);
    }

    public final void visitAttributeOnmousewheel(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmousewheel=\"", attributeValue);
    }

    public final void visitAttributeOnseeking(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onseeking=\"", attributeValue);
    }

    public final void visitAttributeOnblur(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onblur=\"", attributeValue);
    }

    public final void visitAttributeOnvolumenchange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onvolumenchange=\"", attributeValue);
    }

    public final void visitAttributeMax(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " max=\"", attributeValue);
    }

    public final void visitAttributeContextmenu(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " contextmenu=\"", attributeValue);
    }

    public final void visitAttributeOptimum(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " optimum=\"", attributeValue);
    }

    public final void visitAttributeKeytype(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " keytype=\"", attributeValue);
    }

    public final void visitAttributeOndurationchange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondurationchange=\"", attributeValue);
    }

    public final void visitAttributeOnplaying(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onplaying=\"", attributeValue);
    }

    public final void visitAttributeOnended(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onended=\"", attributeValue);
    }

    public final void visitAttributeOnloadeddata(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onloadeddata=\"", attributeValue);
    }

    public final void visitAttributeClassid(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " classid=\"", attributeValue);
    }

    public final void visitAttributeScoped(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " scoped=\"", attributeValue);
    }

    public final void visitAttributeAcceptCharset(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " accept-charset=\"", attributeValue);
    }

    public final void visitAttributeValidationGroup(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ValidationGroup=\"", attributeValue);
    }

    public final void visitAttributeOnmouseout(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmouseout=\"", attributeValue);
    }

    public final void visitAttributeOnsuspend(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onsuspend=\"", attributeValue);
    }

    public final void visitAttributeWidth(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " width=\"", attributeValue);
    }

    public final void visitAttributeOnwaiting(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onwaiting=\"", attributeValue);
    }

    public final void visitAttributeCite(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " cite=\"", attributeValue);
    }

    public final void visitAttributeOncanplay(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " oncanplay=\"", attributeValue);
    }

    public final void visitAttributeOnmousedown(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmousedown=\"", attributeValue);
    }

    public final void visitAttributeAutobuffer(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " autobuffer=\"", attributeValue);
    }

    public final void visitAttributeWrap(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " wrap=\"", attributeValue);
    }

    public final void visitAttributeOpen(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " open=\"", attributeValue);
    }

    public final void visitAttributeOnemptied(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onemptied=\"", attributeValue);
    }

    public final void visitAttributeSpan(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " span=\"", attributeValue);
    }

    public final void visitAttributeControls(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " controls=\"", attributeValue);
    }

    public final void visitAttributeRunat(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " runat=\"", attributeValue);
    }

    public final void visitAttributeData(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " data=\"", attributeValue);
    }

    public final void visitAttributeOnLoad(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnLoad=\"", attributeValue);
    }

    public final void visitAttributeSubject(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " subject=\"", attributeValue);
    }

    public final void visitAttributeTabIndex(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " tabIndex=\"", attributeValue);
    }

    public final void visitAttributeDir(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " dir=\"", attributeValue);
    }

    public final void visitAttributeOnDataBinding(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnDataBinding=\"", attributeValue);
    }

    public final void visitAttributeOnresize(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onresize=\"", attributeValue);
    }

    public final void visitAttributeFormaction(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " formaction=\"", attributeValue);
    }

    public final void visitAttributeHigh(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " high=\"", attributeValue);
    }

    public final void visitAttributeDatetime(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " datetime=\"", attributeValue);
    }

    public final void visitAttributeAccesskey(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " accesskey=\"", attributeValue);
    }

    public final void visitAttributeOnPreRender(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnPreRender=\"", attributeValue);
    }

    public final void visitAttributeLoop(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " loop=\"", attributeValue);
    }

    public final void visitAttributeRowspan(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " rowspan=\"", attributeValue);
    }

    public final void visitAttributeOnpause(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onpause=\"", attributeValue);
    }

    public final void visitAttributeRel(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " rel=\"", attributeValue);
    }

    public final void visitAttributeOnloadstart(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onloadstart=\"", attributeValue);
    }

    public final void visitAttributeOnprogress(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onprogress=\"", attributeValue);
    }

    public final void visitAttributeChecked(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " checked=\"", attributeValue);
    }

    public final void visitAttributeOnbeforeprint(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onbeforeprint=\"", attributeValue);
    }

    public final void visitAttributeSelected(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " selected=\"", attributeValue);
    }

    public final void visitAttributeOnstorage(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onstorage=\"", attributeValue);
    }

    public final void visitAttributeFormmethod(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " formmethod=\"", attributeValue);
    }

    public final void visitAttributeOnforminput(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onforminput=\"", attributeValue);
    }

    public final void visitAttributeAlt(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " alt=\"", attributeValue);
    }

    public final void visitAttributeList(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " list=\"", attributeValue);
    }

    public final void visitAttributeAutoplay(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " autoplay=\"", attributeValue);
    }

    public final void visitAttributeOnDisposed(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnDisposed=\"", attributeValue);
    }

    public final void visitAttributeOntimeupdate(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ontimeupdate=\"", attributeValue);
    }

    public final void visitAttributeHreflang(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " hreflang=\"", attributeValue);
    }

    public final void visitAttributeFormnovalidate(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " formnovalidate=\"", attributeValue);
    }

    public final void visitAttributeName(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " name=\"", attributeValue);
    }

    public final void visitAttributeFiles(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " files=\"", attributeValue);
    }

    public final void visitAttributeOnabort(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onabort=\"", attributeValue);
    }

    public final void visitAttributeOnloadedmetadata(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onloadedmetadata=\"", attributeValue);
    }

    public final void visitAttributeDefer(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " defer=\"", attributeValue);
    }

    public final void visitAttributeContenteditable(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " contenteditable=\"", attributeValue);
    }

    public final void visitAttributeHttpEquiv(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " http-equiv=\"", attributeValue);
    }

    public final void visitAttributeHidden(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " hidden=\"", attributeValue);
    }

    public final void visitAttributeOnerror(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onerror=\"", attributeValue);
    }

    public final void visitAttributeOnmouseup(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onmouseup=\"", attributeValue);
    }

    public final void visitAttributeEnableTheming(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " EnableTheming=\"", attributeValue);
    }

    public final void visitAttributeOndragover(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondragover=\"", attributeValue);
    }

    public final void visitAttributeOnhashchange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onhashchange=\"", attributeValue);
    }

    public final void visitAttributeOnonline(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ononline=\"", attributeValue);
    }

    public final void visitAttributeFor(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " for=\"", attributeValue);
    }

    public final void visitAttributeItemprop(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " itemprop=\"", attributeValue);
    }

    public final void visitAttributeMedia(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " media=\"", attributeValue);
    }

    public final void visitAttributeTitle(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " title=\"", attributeValue);
    }

    public final void visitAttributeOnformchange(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onformchange=\"", attributeValue);
    }

    public final void visitAttributeContent(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " content=\"", attributeValue);
    }

    public final void visitAttributeSkinID(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " SkinID=\"", attributeValue);
    }

    public final void visitAttributeOninput(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " oninput=\"", attributeValue);
    }

    public final void visitAttributeOnstalled(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onstalled=\"", attributeValue);
    }

    public final void visitAttributeScope(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " scope=\"", attributeValue);
    }

    public final void visitAttributePlaceholder(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " placeholder=\"", attributeValue);
    }

    public final void visitAttributeOnsubmit(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onsubmit=\"", attributeValue);
    }

    public final void visitAttributeLang(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " lang=\"", attributeValue);
    }

    public final void visitAttributeValue(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " value=\"", attributeValue);
    }

    public final void visitAttributeCols(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " cols=\"", attributeValue);
    }

    public final void visitAttributeClass(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " class=\"", attributeValue);
    }

    public final void visitAttributeOndragleave(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondragleave=\"", attributeValue);
    }

    public final void visitAttributeSummary(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " summary=\"", attributeValue);
    }

    public final void visitAttributeHeaders(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " headers=\"", attributeValue);
    }

    public final void visitAttributeSrc(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " src=\"", attributeValue);
    }

    public final void visitAttributeAutocomplete(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " autocomplete=\"", attributeValue);
    }

    public final void visitAttributeManifest(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " manifest=\"", attributeValue);
    }

    public final void visitAttributeStart(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " start=\"", attributeValue);
    }

    public final void visitAttributeSandbox(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " sandbox=\"", attributeValue);
    }

    public final void visitAttributeMultiple(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " multiple=\"", attributeValue);
    }

    public final void visitAttributeOnunload(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onunload=\"", attributeValue);
    }

    public final void visitAttributeOndrop(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondrop=\"", attributeValue);
    }

    public final void visitAttributeLabel(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " label=\"", attributeValue);
    }

    public final void visitAttributeAutofocus(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " autofocus=\"", attributeValue);
    }

    public final void visitAttributeRows(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " rows=\"", attributeValue);
    }

    public final void visitAttributeOndragstart(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " ondragstart=\"", attributeValue);
    }

    public final void visitAttributeOnundo(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onundo=\"", attributeValue);
    }

    public final void visitAttributeOnServerClick(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " OnServerClick=\"", attributeValue);
    }

    public final void visitAttributeTarget(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " target=\"", attributeValue);
    }

    public final void visitAttributeFormtarget(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " formtarget=\"", attributeValue);
    }

    public final void visitAttributeOnredo(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onredo=\"", attributeValue);
    }

    public final void visitAttributeAsync(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " async=\"", attributeValue);
    }

    public final void visitAttributeOnshow(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onshow=\"", attributeValue);
    }

    public final void visitAttributeForm(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " form=\"", attributeValue);
    }

    public final void visitAttributeDataMsgId(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " data-MsgId=\"", attributeValue);
    }

    public final void visitAttributeCausesValidation(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " CausesValidation=\"", attributeValue);
    }

    public final void visitAttributeChallenge(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " challenge=\"", attributeValue);
    }

    public final void visitAttributeStep(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " step=\"", attributeValue);
    }

    public final void visitAttributeReversed(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " reversed=\"", attributeValue);
    }

    public final void visitAttributeOnpopstate(String attributeValue) {
        HtmlTags.appendAttribute2(sb, " onpopstate=\"", attributeValue);
    }
}
