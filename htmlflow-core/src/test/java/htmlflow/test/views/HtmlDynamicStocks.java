/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt)
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

package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.test.model.Stock;
import org.xmlet.htmlapifaster.EnumHttpEquivType;
import org.xmlet.htmlapifaster.EnumMediaType;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

public class HtmlDynamicStocks {

    public static HtmlView stocksViewOk = HtmlFlow.view(HtmlDynamicStocks::templateStocksOk);

    private static void templateStocksOk(HtmlPage page) {
        page
            .html()
                .head()
                    .title().text("Stock Prices").__()
                    .meta()
                        .attrHttpEquiv(EnumHttpEquivType.CONTENT_TYPE)
                        .attrContent("text/html; charset=UTF-8")
                    .__()
                    .meta()
                        .addAttr("http-equiv", "Content-Style-Type")
                        .attrContent("text/CSS")
                    .__()
                    .meta()
                        .addAttr("http-equiv", "Content-Script-Type")
                        .attrContent("text/javascript")
                    .__()
                    .link()
                        .addAttr("rel", "shortcut icon")
                        .attrHref("/images/favicon.ico")
                    .__()
                    .link()
                        .attrRel(EnumRelType.STYLESHEET)
                        .attrType(EnumTypeContentType.TEXT_CSS)
                        .attrHref("/CSS/style.CSS")
                        .attrMedia(EnumMediaType.ALL)
                    .__()
                    .script()
                        .attrType(EnumTypeScriptType.TEXT_JAVASCRIPT)
                        .attrSrc("/js/util.js")
                    .__()
                .__() // head
                .body()
                    .h1().text("Stock Prices").__()
                    .table()
                        .thead()
                            .tr()
                                .th().text("#").__()
                                .th().text("symbol").__()
                                .th().text("name").__()
                                .th().text("price").__()
                                .th().text("change").__()
                                .th().text("ratio").__()
                            .__() // tr
                        .__() // thead
                        .tbody()
                        .<Iterable<Stock>>dynamic((tbody, stocks) -> stocks.forEach(stock ->
                            tbody
                                .tr().attrClass(stock.getIndex() % 2 == 0 ? "even" : "odd")
                                .td().text(stock.getIndex()).__()
                                .td()
                                    .a().attrHref("/stocks/" + stock.getSymbol()).text(stock.getSymbol()).__()
                                .__()
                                .td()
                                    .a().attrHref(stock.getUrl()).text(stock.getName()).__()
                                .__()
                                .td().strong().text(stock.getPrice()).__().__()
                                .td()
                                    .of(td -> {
                                        double change = stock.getChange();
                                        if (change < 0) {
                                            td.attrClass("minus");
                                        }
                                        td.text(change);
                                    })
                                .__()
                                .td()
                                    .of(td -> {
                                        double ratio = stock.getRatio();
                                        if (ratio < 0) {
                                            td.attrClass("minus");
                                        }
                                        td.text(ratio);
                                    })
                                .__()
                            .__()))
                    .__() // tbody
                .__() // table
            .__() // body
        .__(); // html
    }
}
