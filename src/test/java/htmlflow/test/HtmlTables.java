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
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Div;
import org.xmlet.htmlapi.EnumRelLinkType;
import org.xmlet.htmlapi.EnumTypeContentType;
import org.xmlet.htmlapi.Html;
import org.xmlet.htmlapi.Table;
import org.xmlet.htmlapi.Tr;

import static java.util.stream.IntStream.range;

public class HtmlTables {

    static final HtmlView<?> simpleTableView(int[][] output){
        Table<Div<Body<Html<HtmlView<Object>>>>> t = HtmlView.html()
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
                        .º();//tr
        range(0, output.length)
            .forEach(i -> {
                Tr tr = t.tr();
                range(0, output.length).forEach(j ->
                    tr.td().text("" + output[i][j])
                );
            });
        return t
                    .º() //table
                .º() //div
            .º() //body
        .º();//html
    }

    static final HtmlView<Iterable<Task>> taskListView = HtmlView
        .<Iterable<Task>>html()
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
                .a().attrHref("https://github.com/fmcarvalho/HtmlFlow").text("HtmlFlow").º()
                .p().text("Html page built with HtmlFlow.").º()
                .h1().text("Task List").º()
                .hr().º()
                .div()
                    .table()
                        .attrClass("table")
                        .tr()
                            .th().text("Title").º()
                            .th().text("Description").º()
                            .th().text("Priority").º()
                        .º()
                        // prior version 2.0: table.trFromIterable(Task::getTitle, Task::getDescription, Task::getPriority);
                        .<Iterable<Task>>binder((tbl, list) -> {
                            list.forEach(task -> {
                                tbl
                                    .tr()
                                        .td().text(task.getTitle()).º()
                                        .td().text(task.getDescription()).º()
                                        .td().text(task.getPriority().toString()).º()
                                    .º(); // tr
                            });
                        })
                    .º() // table
                .º() // div
            .º() // body
        .º(); // html

    static final HtmlView<Iterable<Task>> taskTableView = HtmlView
        .<Iterable<Task>>html()
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
                        /*
                         * Adds a dynamic Tr, which creates new Tr elements for each item
                         * contained in the model received as argument of the write method.
                         */
                        // t.trFromIterable(binderGetTitle(), binderGetDescription(), binderGetPriority());
                        .<Iterable<Task>>binder((tbl, list) -> {
                            list.forEach(item -> {
                                tbl.tr()
                                    .td().text(item.getTitle()).º()
                                    .td().text(item.getDescription()).º()
                                    .td().text(item.getPriority().toString());
                            });
                        })
                    .º() // table
                .º() // div
            .º() // body
        .º(); // html

}
