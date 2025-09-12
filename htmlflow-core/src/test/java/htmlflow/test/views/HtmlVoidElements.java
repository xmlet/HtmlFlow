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
import htmlflow.HtmlView;
import org.xmlet.htmlapifaster.EnumShapeType;
import org.xmlet.htmlapifaster.EnumTypeInputType;

public class HtmlVoidElements {
    public static HtmlView voidElements = HtmlFlow.view(view -> view
        .html()
            .head()
                .title()
                    .text("Html View with somid elements")
                .__()
                .base()
                    .attrHref("http://www.example.com/page.html")
                .__()
            .__()
            .body()
                .embed()
                    .attrType("video/webm")
                    .attrSrc("/media/stream_of_water_audioless.webm")
                    .attrWidth("300")
                    .attrHeight("200")
                .__()
                .input()
                    .attrType(EnumTypeInputType.TEXT)
                    .attrId("display-name")
                    .attrPattern("[A-Za-z\\s]+")
                    .attrMaxlength(5L)
                    .attrValue("Aa")
                    .attrRequired(true)
                .__()
                .table()
                    .colgroup()
                        .col().__()
                        .col().attrSpan(2L).attrClass("batman").__()
                        .col().attrSpan(2L).attrClass("flash").__()
                    .__()
                .__()
                .video()
                    .source()
                        .attrSrc("/media/examples/stream_of_water.webm")
                        .attrType("video/webm")
                    .__()
                .__()
                .area()
                    .attrShape(EnumShapeType.CIRCLE)
                    .attrCoords("130,136,60")
                    .attrHref("https://developer.mozilla.org/")
                    .attrTarget("_blank")
                    .attrAlt("MDN")
                .__()
                .object()
                    .param()
                        .attrName("dummy")
                    .__()
                .__()
            .__()
        .__()
    );
}