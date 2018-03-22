/*
 * MIT License
 *
 * Copyright (c) 2014-16, Miguel Gamboa (gamboa.pt)
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
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Div;
import org.xmlet.htmlapi.EnumRelLinkType;
import org.xmlet.htmlapi.EnumTypeContentType;
import org.xmlet.htmlapi.Head;
import org.xmlet.htmlapi.Html;
import org.xmlet.htmlapi.Table;
import org.xmlet.htmlapi.Tr;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static htmlflow.test.Utils.html;
import static java.util.stream.Collectors.toList;
import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 */
public class TestTable {

    private static final Logger LOGGER = Logger.getLogger("htmlflow.test");

    @Test
    public void test_simple_table() throws ParserConfigurationException, SAXException, IOException{
        /*
         * Arrange
         */
        HtmlView<?> view = new HtmlView();
        view
                .head()
                .title()
                .text("Dummy Table");

        Table t = view.body()
                .h1().text("Dummy Table").º()
                .hr().º()
                .div()
                .table();

        Tr headerRow = t.tr();
        headerRow.th().text("Id1");
        headerRow.th().text("Id2");
        headerRow.th().text("Id3");
        /*
         * Act
         */
        int[][] output = {{1,2,3},{4,5,6}, {7,8,9}};
        for (int i = 0; i < output.length; i++) {
            Tr tr = t.tr();
            for (int j = 0; j < output[i].length; j++) {
                tr.td().text(Integer.toString(output[i][j]));
            }
        }
        view.write();
        /*
         * Assert
         */
        Element elem = Utils.getRootElement(view.toByteArray());
        assertEquals(Html.class.getSimpleName().toLowerCase(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(Head.class.getSimpleName().toLowerCase(), childNodes.item(1).getNodeName());
        assertEquals(Body.class.getSimpleName().toLowerCase(), childNodes.item(3).getNodeName());
        Node div = childNodes.item(3).getChildNodes().item(5);
        assertEquals(Div.class.getSimpleName().toLowerCase(), div.getNodeName());
        Node table = div.getChildNodes().item(1);
        assertEquals(Table.class.getSimpleName().toLowerCase(), table.getNodeName());
        for (int i = 0; i < output.length; i++) {
            Node tr = table.getChildNodes().item(3 + i*2);
            assertEquals(Tr.class.getSimpleName().toLowerCase(), tr.getNodeName());
            for (int j = 0; j < output[i].length; j++) {
                Node td = tr.getChildNodes().item(j*2 + 1);
                assertEquals("td", td.getNodeName());
                String val = td.getFirstChild().getNodeValue().replaceAll("\\s+","");
                assertEquals(output[i][j], Integer.parseInt(val));
            }
        }
        // LOGGER.log(Level.INFO, mem.toString());
    }
    @Test
    public void test_table_with_binding() throws ParserConfigurationException, SAXException, IOException{
        /*
         * Arrange
         */
        HtmlView<Iterable<Task>> view = new HtmlView<>();
        view
                .head()
                .title()
                .text("Dummy Table");

        Table<Div<Body<Html>>> t = view.body()
                .h1()
                .text("Dummy Table")
                .º()
                .hr()
                .º()
                .div()
                .table();

        Tr<Table<Div<Body<Html>>>> headerRow = t.tr();
        headerRow.th().text("Title");
        headerRow.th().text("Description");
        headerRow.th().text("Priority");
        /*
         * Adds a dynamic Tr, which creates new Tr elements for each item
         * contained in the model received as argument of the write method.
         */
        // t.trFromIterable(binderGetTitle(), binderGetDescription(), binderGetPriority());
        t.<List<Task>>binder((tbl, list) -> {
            list.forEach(item -> {
                tbl.tr()
                        .td().text(item.getTitle()).º()
                        .td().text(item.getDescription()).º()
                        .td().text(item.getPriority().toString());
            });
        });
        /*
         * Act
         */
        Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High, Status.Progress);
        Task t2 = new Task("Special dinner", "Have dinner with someone!", Priority.Normal, Status.Completed);
        Task t3 = new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High, Status.Deferred);
        List<Task> output = Arrays.asList(t1, t2, t3);
        view.write(output);
        /*
         * Assert
         */
        Element elem = Utils.getRootElement(view.toByteArray());
        assertEquals(Html.class.getSimpleName().toLowerCase(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(Head.class.getSimpleName().toLowerCase(), childNodes.item(1).getNodeName());
        assertEquals(Body.class.getSimpleName().toLowerCase(), childNodes.item(3).getNodeName());
        Node div = childNodes.item(3).getChildNodes().item(5);
        assertEquals(Div.class.getSimpleName().toLowerCase(), div.getNodeName());
        Node table = div.getChildNodes().item(1);
        assertEquals(Table.class.getSimpleName().toLowerCase(), table.getNodeName());
        for (int i = 0; i < output.size(); i++) {
            Node tr = table.getChildNodes().item(3 + i*2);
            assertEquals(Tr.class.getSimpleName().toLowerCase(), tr.getNodeName());
            /*
             * Check title
             */
            String title = tr.getChildNodes().item(1).getFirstChild().getNodeValue().trim();
            assertEquals(output.get(i).getTitle(), title);
            /*
             * Check task description
             */
            String desc = tr.getChildNodes().item(3).getFirstChild().getNodeValue().trim();
            assertEquals(output.get(i).getDescription(), desc);
            /*
             * Check task priority
             */
            String prio = tr.getChildNodes().item(5).getFirstChild().getNodeValue().trim();
            assertEquals(output.get(i).getPriority().toString(), prio);
        }
        // LOGGER.log(Level.INFO, mem.toString());
    }

    @Test
    public void test_table_binding_for_readme_twice() throws IOException, ParserConfigurationException, SAXException {
        HtmlView<Iterable<Task>> taskView = taskListView();
        validateBindingTable(taskView);
        validateBindingTable(taskView);
    }

    static void validateBindingTable(HtmlView<Iterable<Task>> taskView){
        List<Task> dataSource = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        List<String> actual = html(taskView, dataSource).collect(toList());
        Iterator<String> iter = actual.iterator();
        actual.forEach(System.out::println);
        Utils
                .loadLines("TaskList.html")
                .forEach(expected -> assertEquals(expected, iter.next()));

    }
    private static HtmlView<Iterable<Task>> taskListView(){
        HtmlView<Iterable<Task>> taskView = new HtmlView<>();
        taskView
                .head().title().text("Task List").º()
                .link()
                .attrRel(EnumRelLinkType.STYLESHEET)
                .attrType(EnumTypeContentType.TEXTCSS)
                .attrHref("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        Body<Html> body = taskView.body();
        body.a().attrHref("https://github.com/fmcarvalho/HtmlFlow").text("HtmlFlow");
        body.p().text("Html page built with HtmlFlow.");
        Table<Div<Body<Html>>> table = body
                .attrClass("container")
                .h1().text("Task List").º()
                .hr().º()
                .div()
                .table()
                .attrClass("table")
                .tr()
                    .th().text("Title").º()
                    .th().text("Description").º()
                    .th().text("Priority").º()
                .º();
        // table.trFromIterable(Task::getTitle, Task::getDescription, Task::getPriority);
        table.<List<Task>>binder((tbl, list) -> {
            list.forEach(task -> {
                tbl.tr()
                        .td().text(task.getTitle()).º()
                        .td().text(task.getDescription()).º()
                        .td().text(task.getPriority().toString());
            });
        });

        return taskView;
    }
}
