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
import htmlflow.StaticHtml;
import htmlflow.test.model.Task;
import org.xmlet.htmlapifaster.EnumEnctypeForm;
import org.xmlet.htmlapifaster.EnumMethodForm;
import org.xmlet.htmlapifaster.EnumRelLinkType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.htmlapifaster.EnumTypeScript;

public class HtmlLists {
    static final String divClass = "divClass";
    static final String divId = "divId";

    public static final void taskView(DynamicHtml<Task> view, Task task) {
        view
            .html()
                .head()
                    .script()
                        .attrType(EnumTypeScript.TEXT_JAVASCRIPT)
                        .attrSrc("test.css")
                    .º() //script
                .º() // head
                .body()
                    .div()
                    .º() //div
                    .div()
                        .attrId(divId)
                        .attrClass(divClass)
                        // !!!!!! Missing feature in HtmlApiFaster !!!!
                        // .addAttr(new BaseAttribute("tutu", "toto"))
                        .form()
                            .attrAction("/action.do")
                            .attrMethod(EnumMethodForm.POST)
                            .attrEnctype(EnumEnctypeForm.APPLICATION_X_WWW_FORM_URLENCODED)
                        .º() //form
                    .º() //div
                .º() //body
            .º(); //html
    }

    public static HtmlView viewDetails = StaticHtml.view()
            .html()
                .head()
                    .title().text("Task Details").º()
                    .link()
                        .attrRel(EnumRelLinkType.STYLESHEET)
                        .attrType(EnumTypeContentType.TEXT_CSS)
                        .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                    .º() //link
                .º() //head
                .body()
                    .attrClass("container")
                    .h1().text("Task Details").º()
                    .hr().º()
                    .div().text("Title: ISEL MPD project")
                        .br().º()
                        .text("Description: A Java library for serializing objects in HTML.")
                        .br().º()
                        .text("Priority: HIGH")
                    .º() //div
                .º() //body
            .º(); //html

    public static final void taskDetailsView(DynamicHtml<Task> view, Task task) {
        view
            .html()
                .head()
                    .title().text("Task Details").º()
                    .link()
                        .attrRel(EnumRelLinkType.STYLESHEET)
                        .attrType(EnumTypeContentType.TEXT_CSS)
                        .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                    .º() //link
                .º() //head
                .body()
                    .attrClass("container")
                    .h1().text("Task Details").º()
                    .hr().º()
                    .div()
                        .text("Title:").text(task.getTitle())
                        .br().º()
                        .text("Description:").text(task.getDescription())
                        .br().º()
                        .text("Priority:").text(task.getPriority() + "")
                    .º() // div
                .º() //body
            .º(); // html
    }
}
