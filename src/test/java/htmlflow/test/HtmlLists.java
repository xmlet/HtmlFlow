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
import htmlflow.test.model.Task;
import org.xmlet.htmlapi.BaseAttribute;
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.EnumEnctypeForm;
import org.xmlet.htmlapi.EnumMethodForm;
import org.xmlet.htmlapi.EnumRelLinkType;
import org.xmlet.htmlapi.EnumTypeContentType;
import org.xmlet.htmlapi.EnumTypeScript;
import org.xmlet.htmlapi.Html;

public class HtmlLists {
    static final String divClass = "divClass";
    static final String divId = "divId";

    static final HtmlView<Task> taskView = HtmlView
        .<Task>html()
            .body()
                .div()
                .º() //div
            .º()//body
            .head()
                .script()
                    .attrType(EnumTypeScript.TEXT_JAVASCRIPT)
                    .attrSrc("test.css")
                .º() //script
            .º() // head
            .<Body<Html<HtmlView<Task>>>>find(elem -> elem instanceof Body)
            .findFirst() // get previously created body
            .get()
                .div()
                    .attrId(divId)
                    .attrClass(divClass)
                    .addAttr(new BaseAttribute("tutu", "toto"))
                    .form()
                        .attrAction("/action.do")
                        .attrMethod(EnumMethodForm.POST)
                        .attrEnctype(EnumEnctypeForm.APPLICATION_X_WWW_FORM_URLENCODED)
                    .º() //form
                .º() //div
            .º() //body
        .º(); //html


    static final HtmlView viewDetails = HtmlView
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

    static final HtmlView<Task> taskDetailsView = HtmlView
        .<Task>html()
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
                    .text("Title:").text(Task::getTitle)
                    .br().º()
                    .text("Description:").text(Task::getDescription)
                    .br().º()
                    .text("Priority:").text(Task::getPriority)
                .º() // div
            .º() //body
        .º(); // html

}
