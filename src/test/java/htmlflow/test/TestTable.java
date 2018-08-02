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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static htmlflow.test.Utils.htmlWrite;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 */
public class TestTable {

    private static final Logger LOGGER = Logger.getLogger("htmlflow.test");

    @Test
    public void test_simple_table_render() throws ParserConfigurationException, SAXException, IOException{
        /*
         * Arrange
         */
        int[][] output = {{1,2,3},{4,5,6}, {7,8,9}};
        HtmlView<?> view = simpleTableView(output);
        /*
         * Act
         */
        String html = view.render();
        /*
         * Assert
         */
        assertSimpleHtmlView(html.getBytes(), output);
    }
    @Test
    public void test_simple_table_write() throws ParserConfigurationException, SAXException, IOException{
        /*
         * Arrange
         */
        int[][] output = {{1,2,3},{4,5,6}, {7,8,9}};
        HtmlView<?> view = simpleTableView(output);
        /*
         * Act
         */
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        view
                .setPrintStream(new PrintStream(mem))
                .write();
        /*
         * Assert
         */
        assertSimpleHtmlView(mem.toByteArray(), output);
    }
    @Test
    public void test_table_with_binding() throws ParserConfigurationException, SAXException, IOException{
        /*
         * Act
         */
        Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High, Status.Progress);
        Task t2 = new Task("Special dinner", "Have dinner with someone!", Priority.Normal, Status.Completed);
        Task t3 = new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High, Status.Deferred);
        List<Task> output = Arrays.asList(t1, t2, t3);
        String html = taskTableView.render(output);
        /*
         * Assert
         */
        assertTaskHtmlView(html.getBytes(), output);
    }

    @Test
    public void test_table_binding_for_readme_twice() throws IOException, ParserConfigurationException, SAXException {
        validateBindingTable(taskListView);
        validateBindingTable(taskListView);
    }

    static void validateBindingTable(HtmlView<Iterable<Task>> taskView){
        List<Task> dataSource = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        List<String> actual = htmlWrite(taskView, dataSource).collect(toList());
        Iterator<String> iter = actual.iterator();
        actual.forEach(System.out::println);
        Utils
                .loadLines("TaskList.html")
                .forEach(expected -> assertEquals(expected, iter.next()));

    }

    final static HtmlView<Iterable<Task>> taskListView = HtmlView
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

    final static HtmlView<Iterable<Task>> taskTableView = HtmlView
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

    private static void assertTaskHtmlView(byte[] html, List<Task> output) throws UnsupportedEncodingException {
        org.w3c.dom.Element elem = Utils.getRootElement(html);
        assertEquals(Html.class.getSimpleName().toLowerCase(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(Head.class.getSimpleName().toLowerCase(), childNodes.item(0).getNodeName());
        assertEquals(Body.class.getSimpleName().toLowerCase(), childNodes.item(2).getNodeName());
        Node div = childNodes.item(2).getChildNodes().item(5);
        assertEquals(Div.class.getSimpleName().toLowerCase(), div.getNodeName());
        Node table = div.getChildNodes().item(1);
        assertEquals(Table.class.getSimpleName().toLowerCase(), table.getNodeName());
        Node tbody = table.getChildNodes().item(1);
        for (int i = 0; i < output.size(); i++) {
            Node tr = tbody .getChildNodes().item(2 + i*2);
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

    private static HtmlView<?> simpleTableView(int[][] output){
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
            .mapToObj(i -> pair(i, t.tr()))
            .forEach(p -> range(0, output.length).forEach(j -> {
                p.tr.td().text(Integer.toString(output[p.i][j]));
            }));
        return t
                    .º() //table
                .º() //div
            .º() //body
        .º();//html
    }

    static class Pair {
        final int i;
        final Tr<Table<Div<Body<Html<HtmlView<Object>>>>>> tr;

            public Pair(int i, Tr<Table<Div<Body<Html<HtmlView<Object>>>>>> tr) {
            this.i = i;
            this.tr = tr;
        }
    }
    private static Pair pair(int i, Tr<Table<Div<Body<Html<HtmlView<Object>>>>>> tr) {
        return new Pair(i, tr);
    }

    private static void assertSimpleHtmlView(byte[] html, int[][] output) throws UnsupportedEncodingException {
        org.w3c.dom.Element elem = Utils.getRootElement(html);
        assertEquals(Html.class.getSimpleName().toLowerCase(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(Head.class.getSimpleName().toLowerCase(), childNodes.item(0).getNodeName());
        assertEquals(Body.class.getSimpleName().toLowerCase(), childNodes.item(2).getNodeName());
        Node div = childNodes.item(2).getChildNodes().item(5);
        assertEquals(Div.class.getSimpleName().toLowerCase(), div.getNodeName());
        Node table = div.getChildNodes().item(1);
        assertEquals(Table.class.getSimpleName().toLowerCase(), table.getNodeName());
        Node tbody = table.getChildNodes().item(1);
        for (int i = 0; i < output.length; i++) {
            Node tr = tbody.getChildNodes().item(2 + i*2);
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

}
