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

import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import org.xmlet.htmlapifaster.EnumShapeArea;
import org.xmlet.htmlapifaster.EnumTargetBrowsingContext;
import org.xmlet.htmlapifaster.EnumTypeInput;

import java.math.BigInteger;

public class HtmlVoidElements {
    static HtmlView voidElements = StaticHtml.view()
        .html()
            .head()
                .title()
                    .text("Html View with somid elements")
                .º()
                .base()
                    .attrHref("http://www.example.com/page.html")
                .º()
            .º()
            .body()
                .embed()
                    .attrType("video/webm")
                    .attrSrc("/media/stream_of_water_audioless.webm")
                    .attrWidth("300")
                    .attrHeight("200")
                .º()
                .input()
                    .attrType(EnumTypeInput.TEXT)
                    .attrId("display-name")
                    .attrPattern("[A-Za-z\\s]+")
                    .attrMaxlength(5)
                    .attrValue("Aa")
                    .attrRequired("true")
                .º()
                .table()
                    .colgroup()
                        .col().º()
                        .col().attrSpan(new BigInteger("2")).attrClass("batman").º()
                        .col().attrSpan(new BigInteger("2")).attrClass("flash").º()
                    .º()
                .º()
                .video()
                    .source()
                        .attrSrc("/media/examples/stream_of_water.webm")
                        .attrType("video/webm")
                    .º()
                .º()
                .area()
                    .attrShape(EnumShapeArea.CIRCLE)
                    .attrCoords("130,136,60")
                    .attrHref("https://developer.mozilla.org/")
                    .attrTarget(EnumTargetBrowsingContext._BLANK)
                    .attrAlt("MDN")
                .º()
                .object()
                    .param()
                        .attrName("dummy")
                    .º()
                .º()
            .º()
        .º();
}
