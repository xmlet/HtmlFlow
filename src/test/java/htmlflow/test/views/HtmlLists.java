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
import htmlflow.test.model.Task;
import org.xmlet.htmlapifaster.EnumEnctypeType;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.htmlapifaster.EnumTypeScriptType;

public class HtmlLists {
    public static final String divClass = "divClass";
    public static final String divId = "divId";

    public static HtmlPage taskView (Appendable out) {
        return HtmlFlow.doc(out)
            .html()
                .head()
                    .script()
                        .attrType(EnumTypeScriptType.TEXT_JAVASCRIPT)
                        .attrSrc("test.css")
                    .__() //script
                .__() // head
                .body()
                    .div()
                        .comment("A simple dummy comment")
                    .__() //div
                    .div()
                        .attrId(divId)
                        .attrClass(divClass)
                        .addAttr("toto", "tutu")
                        .form()
                            .attrAction("/action.do")
                            .attrMethod(EnumMethodType.POST)
                            .attrEnctype(EnumEnctypeType.APPLICATION_X_WWW_FORM_URLENCODED)
                        .__() //form
                    .__() //div
                .__() //body
            .__(); //html
    }

    public static HtmlView viewDetails = HtmlFlow.view(view -> view
            .html()
                .head()
                    .title().text("Task Details").__()
                    .link()
                        .attrRel(EnumRelType.STYLESHEET)
                        .attrType(EnumTypeContentType.TEXT_CSS)
                        .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                    .__() //link
                .__() //head
                .body()
                    .attrClass("container")
                    .h1().text("Task Details").__()
                    .hr().__()
                    .div().text("Title: ISEL MPD project")
                        .br().__()
                        .text("Description: A Java library for serializing objects in HTML.")
                        .br().__()
                        .text("Priority: HIGH")
                    .__() //div
                .__() //body
            .__() //html
        );
    public static void taskDetailsTemplate(HtmlPage view) {
        view
            .html()
                .head()
                    .title().text("Task Details").__()
                .__() //head
                .body()
                    .<Task>dynamic((body, task) -> body.text("Title:").text(task.getTitle()))
                    .br().__()
                    .<Task>dynamic((body, task) -> body.text("Description:").text(task.getDescription()))
                    .br().__()
                    .<Task>dynamic((body, task) -> body.text("Priority:").text(task.getPriority()))
                .__() //body
            .__(); // html
    }
}
