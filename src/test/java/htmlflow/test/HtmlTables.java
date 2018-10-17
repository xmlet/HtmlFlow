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
import org.xmlet.htmlapifaster.EnumRelLinkType;
import org.xmlet.htmlapifaster.EnumTypeContentType;

public class HtmlTables {

    static void simpleTableView(DynamicHtml<int[][]> view, int[][] outputRows){
        view
            .html()
                .head()
                    .title().text("Dummy Table")
                    .º()// title
                .º()// head
                .body()
                    .h1().text("Dummy Table").º()
                    .hr().º()
                    .div()
                        .table()
                            .tr()
                                .th().text("Id1").º()
                                .th().text("Id2").º()
                                .th().text("Id3").º()
                            .º() //tr
                            .dynamic(table -> {
                                for (int[] outputRow : outputRows) {
                                    table.tr()
                                         .dynamic(tr -> {
                                             for (int rowValue : outputRow) {
                                                 tr.td()
                                                     .text("" + rowValue)
                                                 .º();
                                             }
                                         }).º();
                                }
                            })
                        .º()
                    .º()
                .º()
            .º();
    }

    /**
     * An example of a dynamic view with an Iterable<Task> as domain model and
     * an array with two partial views: a div heading and table row.
     */
    static void taskListViewWithPartials(
        DynamicHtml<Iterable<Task>> view,
        Iterable<Task> tasks,
        HtmlView[] partials)
    {
        view
            .html()
                .head()
                    .title()
                        .text("Task List")
                        .º()
                    .link()
                        .attrRel(EnumRelLinkType.STYLESHEET)
                        .attrType(EnumTypeContentType.TEXT_CSS)
                        .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                    .º()
                .º()
                .body()
                    .attrClass("container")
                    .of(__ -> // ignore body argument because we don't need it here
                        view.addPartial(partials[0]) // taskListViewHeader
                    )
                    .hr().º()
                    .div()
                        .table()
                            .attrClass("table")
                            .tr()
                                .th().text("Title").º()
                                .th().text("Description").º()
                                .th().text("Priority").º()
                            .º()
                            .tbody()
                                .dynamic(tbody ->
                                    tasks.forEach(task -> view.addPartial(partials[1], task)) // taskListRow
                                )
                            .º() // tbody
                        .º() // table
                    .º() // div
                .º() // body
            .º(); // html
    }

    static StaticHtml taskListViewHeader = StaticHtml.view(view -> view
        .div()
            .a().attrHref("https://github.com/fmcarvalho/HtmlFlow").text("HtmlFlow").º()
            .p().text("Html page built with HtmlFlow.").º()
            .h1().text("Task List").º()
        .º() // div
    );

    static DynamicHtml<Task> taskListRow = DynamicHtml.view((view, task) -> {
        view
            .tr()
                .td().dynamic(td -> td.text(task.getTitle())).º()
                .td().dynamic(td -> td.text(task.getDescription())).º()
                .td().dynamic(td -> td.text(task.getPriority().toString())).º()
            .º(); // tr
    });

    public static void taskTableView(DynamicHtml<Iterable<Task>> view, Iterable<Task> tasks){
        view
            .html()
                .head()
                    .title().text("Dummy Table")
                    .º()
                .º()
                .body()
                    .h1().text("Dummy Table").º()
                    .hr().º()
                    .div()
                        .table()
                            .tr()
                                .th().text("Title").º()
                                .th().text("Description").º()
                                .th().text("Priority").º()
                            .º() // tr
                            .dynamic(table ->
                                tasks.forEach(item ->
                                    table
                                        .tr()
                                            .td().text(item.getTitle()).º()
                                            .td().text(item.getDescription()).º()
                                            .td().text(item.getPriority().toString()).º()
                                        .º() // tr
                                )
                            )
                        .º()
                    .º()
                .º()
            .º();
    }

    /**
     * View with a nested table based on issue:
     *    https://github.com/xmlet/HtmlFlow/issues/18
     */
    static HtmlView nestedTable = StaticHtml.view()
            .html()
                .body()
                    .table()
                        .tr()
                            .attrClass("top")
                            .td()
                                .attrColspan("2")
                                .table()
                                    .tr()
                                        .td()
                                            .attrClass("title")
                                            .img()
                                                .attrSrc("logo.png")
                                                .attrStyle("width:100%; max-width:300px;")
                                            .º() // img
                                        .º() // td
                                    .º() // tr
                                .º() // table
                            .º() // td
                            .td()
                                .text("Invoice #: 123")
                                .br().º()
                                .text("Created: January 1, 2015")
                                .br().º()
                                .text("Due: February 1, 2015")
                            .º() // td
                        .º() // tr
                    .º() // table
                .º() // body
            .º(); // html
}