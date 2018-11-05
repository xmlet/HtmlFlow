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

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import htmlflow.StaticHtml;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Task;
import org.junit.Test;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class HtmlForReadme {

    @Test
    public void testSample01() {
        String html= StaticHtml
            .view()
                .html()
                    .head()
                        .title().text("HtmlFlow").__()
                    .__() //head
                    .body()
                        .div().attrClass("container")
                            .h1().text("My first page with HtmlFlow").__()
                            .img().attrSrc("https://avatars1.githubusercontent.com/u/35267172").__()
                            .p().text("Typesafe is awesome! :-)").__()
                        .__()
                    .__() //body
                .__() //html
            .render();
        // System.out.println(html);
    }

    @Test
    public void testSample02() throws IOException {
        String html = view.render();        // 1) Get a string with the HTML

        // System.out.println(html);

        view
            // .setPrintStream(System.out)
            .write();                       // 2) print to the standard output

        view
            // .setPrintStream(new PrintStream(new FileOutputStream("details.html")))
            .write();                       // 3) write to details.html file

        // Desktop.getDesktop().browse(URI.create("details.html"));
    }


    static HtmlView view = StaticHtml.view(v -> v
                .html()
                    .body()
                        .p().text("Typesafe is awesome! :-)").__()
                    .__() //body
                .__()); // html

    @Test
    public void testSample03() throws IOException {
        HtmlView<Task> view = DynamicHtml.view(HtmlLists::taskDetailsTemplate);

        List<Task> tasks = Arrays.asList(
            new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new Task(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
            new Task(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High)
        );
        for (Task task: tasks) {
            Path path = Paths.get("task" + task.getId() + ".html");
            // Files.write(path, view.render(task).getBytes());
            // Desktop.getDesktop().browse(path.toUri());
        }
    }

    static HtmlView<Stream<Task>> tasksTableView = DynamicHtml.view(HtmlForReadme::tasksTableTemplate);

    @Test
    public void testSample04() throws IOException {
        Stream<Task> tasks = Stream.of(
            new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
            new Task(4, "Special dinner", "Moonlight dinner!", Priority.Normal),
            new Task(5, "US Open Final 2018", "Juan Martin del Potro VS  Novak Djokovic", Priority.High)
        );

        Path path = Paths.get("tasksTable.html");
        // Files.write(path, tasksTableView.render(tasks).getBytes());
        // Desktop.getDesktop().browse(path.toUri());
    }

    static void tasksTableTemplate(DynamicHtml<Stream<Task>> view, Stream<Task> tasks) {
        view
            .html()
                .head()
                    .title()
                        .text("Tasks Table")
                    .__()
                .__()
                .body()
                    .table()
                        .attrClass("table")
                        .tr()
                            .th().text("Title").__()
                            .th().text("Description").__()
                            .th().text("Priority").__()
                        .__()
                        .tbody()
                            .dynamic(tbody ->
                                tasks.forEach(task -> tbody
                                    .tr()
                                        .td().dynamic(td -> td.text(task.getTitle())).__()
                                        .td().dynamic(td -> td.text(task.getDescription())).__()
                                        .td().dynamic(td -> td.text(task.getPriority().toString())).__()
                                    .__() // tr
                                ) // forEach
                            ) // dynamic
                        .__() // tbody
                    .__() // table
                .__() // body
            .__(); // html
    }
}
