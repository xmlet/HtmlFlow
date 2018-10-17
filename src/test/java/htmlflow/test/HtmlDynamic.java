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

package htmlflow.test;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import htmlflow.test.model.Stock;
import org.xmlet.htmlapifaster.EnumMediaMediaType;
import org.xmlet.htmlapifaster.EnumRelLinkType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.htmlapifaster.EnumTypeScript;
import org.xmlet.htmlapifaster.Head;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Link;
import org.xmlet.htmlapifaster.Meta;


public class HtmlDynamic {

    public static DynamicHtml<Iterable<Stock>> stocksViewOk = DynamicHtml.view(HtmlDynamic::templateStocksOk);

    public static DynamicHtml<Iterable<Stock>> stocksViewWrong = DynamicHtml.view(HtmlDynamic::templateStocksWrong);

    private static void templateStocksOk(DynamicHtml<Iterable<Stock>> view, Iterable<Stock> stocks) {
        Head<Html<HtmlView>> head = view
            .html()
                .head()
                    .title().text("Stock Prices").º();
        Meta<Head<Html<HtmlView>>> meta = head.meta();
        head.getVisitor().visitAttribute("http-equiv", "Content-Type");
        meta.attrContent("text/html; charset=UTF-8").º();
        meta = head.meta();
        head.getVisitor().visitAttribute("http-equiv", "Content-Style-Type");
        meta.attrContent("text/CSS").º();
        meta = head.meta();
        head.getVisitor().visitAttribute("http-equiv", "Content-Script-Type");
        meta.attrContent("text/javascript").º();
        Link<Head<Html<HtmlView>>> link = head.link();
        head.getVisitor().visitAttribute("rel", "shortcut icon");
        link.attrHref("/images/favicon.ico").º();
        head
                .link().attrRel(EnumRelLinkType.STYLESHEET).attrType(EnumTypeContentType.TEXT_CSS).attrHref("/CSS/style.CSS").attrMedia(EnumMediaMediaType.ALL).º()
                .script().attrType(EnumTypeScript.TEXT_JAVASCRIPT).attrSrc("/js/util.js").º()
            .º() // head
            .body()
                .h1().text("Stock Prices").º()
                .table()
                    .thead()
                        .tr()
                            .th().text("#").º()
                            .th().text("symbol").º()
                            .th().text("name").º()
                            .th().text("price").º()
                            .th().text("change").º()
                            .th().text("ratio").º()
                        .º() // tr
                    .º() // thead
                    .tbody()
                        .dynamic(tbody -> stocks.forEach(stock -> view.addPartial(tableRowView, stock)))
                    .º() // tbody
                .º() // table
            .º() // body
        .º(); // html
    };

    private static void templateStocksWrong(DynamicHtml<Iterable<Stock>> view, Iterable<Stock> stocks) {
        Head<Html<HtmlView>> head = view
            .html()
                .head()
                    .title().text("Stock Prices").º();
        Meta<Head<Html<HtmlView>>> meta = head.meta();
        head.getVisitor().visitAttribute("http-equiv", "Content-Type");
        meta.attrContent("text/html; charset=UTF-8").º();
        meta = head.meta();
        head.getVisitor().visitAttribute("http-equiv", "Content-Style-Type");
        meta.attrContent("text/CSS").º();
        meta = head.meta();
        head.getVisitor().visitAttribute("http-equiv", "Content-Script-Type");
        meta.attrContent("text/javascript").º();
        Link<Head<Html<HtmlView>>> link = head.link();
        head.getVisitor().visitAttribute("rel", "shortcut icon");
        link.attrHref("/images/favicon.ico").º();
        head
                .link().attrRel(EnumRelLinkType.STYLESHEET).attrType(EnumTypeContentType.TEXT_CSS).attrHref("/CSS/style.CSS").attrMedia(EnumMediaMediaType.ALL).º()
                .script().attrType(EnumTypeScript.TEXT_JAVASCRIPT).attrSrc("/js/util.js").º()
            .º() // head
            .body()
                .h1().text("Stock Prices").º()
                .table()
                    .thead()
                        .tr()
                            .th().text("#").º()
                            .th().text("symbol").º()
                            .th().text("name").º()
                            .th().text("price").º()
                            .th().text("change").º()
                            .th().text("ratio").º()
                        .º() // tr
                    .º() // thead
                    .tbody()
                        // !!!!! Here it is the wrong use with of() for the unit test !!!
                        .of(tbody -> stocks.forEach(stock -> view.addPartial(tableRowView, stock)))
                    .º() // tbody
                .º() // table
            .º() // body
        .º(); // html
    };

    static final DynamicHtml<Stock> tableRowView = DynamicHtml.view((view, stock) -> {
            view
                .tr()
                    .dynamic(tr -> tr.attrClass(stock.getIndex() % 2 == 0 ? "even" : "odd"))
                    .td()
                        .dynamic(td -> td.text(stock.getIndex()))
                    .º()
                    .td()
                        .a().dynamic(a -> a.attrHref("/stocks/" + stock.getSymbol()).text(stock.getSymbol())).º()
                    .º()
                    .td()
                        .a().dynamic(a -> a.attrHref(stock.getUrl()).text(stock.getName())).º()
                    .º()
                    .td()
                        .strong().dynamic(strong -> strong.text(stock.getPrice())).º()
                    .º()
                    .td()
                        .dynamic(td -> {
                        	double change = stock.getChange();

                            if (change < 0) {
                                td.attrClass("minus");
                            }

                            td.text(change);
                        })
                    .º()
                    .td()
                        .dynamic(td -> {
                        	double ratio = stock.getRatio();

                            if (ratio < 0) {
                                td.attrClass("minus");
                            }

                            td.text(ratio);
                        })
                    .º()
                .º();
    });
}
