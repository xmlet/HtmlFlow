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
import htmlflow.HtmlTemplate;
import htmlflow.HtmlView;
import htmlflow.test.model.Task;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.htmlapifaster.Tbody;

import java.util.List;
import java.util.function.BiConsumer;

public class HtmlTables {

    public static void simpleTableView(HtmlPage view){
        view
            .html()
                .head()
                    .title().text("Dummy Table")
                    .__()// title
                .__()// head
                .body()
                    .h1().text("Dummy Table").__()
                    .hr().__()
                    .div()
                        .table()
                            .tr()
                                .th().text("Id1").__()
                                .th().text("Id2").__()
                                .th().text("Id3").__()
                            .__() //tr
                            .<int[][]>dynamic((table, outputRows) -> {
                                for (int[] outputRow : outputRows) {
                                    table.tr()
                                         .of(tr -> {
                                             for (int rowValue : outputRow) {
                                                 tr.td()
                                                     .text("" + rowValue)
                                                 .__();
                                             }
                                         }).__();
                                }
                            })
                        .__()
                    .__()
                .__()
            .__();
    }

    /**
     * An example of a dynamic view with an Iterable<Task> as domain model and
     * an array with two partial views: a div heading and table row.
     */
    public static HtmlTemplate taskListViewWithPartials(BiConsumer<Tbody<?>, Task> partial) {
        return view -> view
            .html()
                .head()
                    .title()
                        .text("Task List")
                        .__()
                    .link()
                        .attrRel(EnumRelType.STYLESHEET)
                        .attrType(EnumTypeContentType.TEXT_CSS)
                        .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
                    .__()
                .__()
                .body()
                    .attrClass("container")
                    .of(__ -> // ignore body argument because we don't need it here
                        HtmlTables.taskListViewHeader(view)
                    )
                    .hr().__()
                    .div()
                        .table()
                            .attrClass("table")
                            .tr()
                                .th().text("Title").__()
                                .th().text("Description").__()
                                .th().text("Priority").__()
                            .__()
                            .tbody()
                                .<Iterable<Task>>dynamic((tbody, tasks) ->
                                    tasks.forEach(task -> partial.accept(tbody, task)) // taskListRow
                                )
                            .__() // tbody
                        .__() // table
                    .__() // div
                .__() // body
            .__(); // html
    }

    public static HtmlPage taskListViewHeader(HtmlPage view) {
        return view
            .div()
                .a().attrHref("https://github.com/fmcarvalho/HtmlFlow").text("HtmlFlow").__()
                .p().text("Html page built with HtmlFlow.").__()
                .h1().text("Task List").__()
            .__(); // div
    }

    public static void taskListRow(Tbody<?> tbody, Task task) {
        tbody
            .tr()
                .td().text(task.getTitle()).__()
                .td().text(task.getDescription()).__()
                .td().text(task.getPriority().toString()).__()
            .__(); // tr
    }

    public static void taskTableView(HtmlPage view){
        view
            .html()
                .head()
                    .title().text("Dummy Table")
                    .__()
                .__()
                .body()
                    .h1().text("Dummy Table").__()
                    .hr().__()
                    .div()
                        .table()
                            .tr()
                                .th().text("Title").__()
                                .th().text("Description").__()
                                .th().text("Priority").__()
                            .__() // tr
                            .<Iterable<Task>>dynamic((table, tasks) ->
                                tasks.forEach(item ->
                                    table
                                        .tr()
                                            .td().text(item.getTitle()).__()
                                            .td().text(item.getDescription()).__()
                                            .td().text(item.getPriority().toString()).__()
                                        .__() // tr
                                )
                            )
                        .__()
                    .__()
                .__()
            .__();
    }

    /**
     * View with a nested table based on issue:
     *    https://github.com/xmlet/HtmlFlow/issues/18
     */
    public static HtmlView nestedTable = HtmlFlow.view(view -> view
            .html()
                .body()
                    .table()
                        .tr()
                            .attrClass("top")
                            .td()
                                .attrColspan(2)
                                .table()
                                    .tr()
                                        .td()
                                            .attrClass("title")
                                            .img()
                                                .attrSrc("logo.png")
                                                .attrStyle("width:100%; max-width:300px;")
                                            .__() // img
                                        .__() // td
                                    .__() // tr
                                .__() // table
                            .__() // td
                            .td()
                                .text("Invoice #: 123")
                                .br().__()
                                .text("Created: January 1, 2015")
                                .br().__()
                                .text("Due: February 1, 2015")
                            .__() // td
                        .__() // tr
                    .__() // table
                .__() // body
            .__() // html
    );
}
