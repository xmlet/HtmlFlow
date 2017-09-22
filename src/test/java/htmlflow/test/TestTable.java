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
import htmlflow.ModelBinder;
import htmlflow.elements.ElementType;
import htmlflow.elements.HtmlBody;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import junit.framework.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static htmlflow.test.Utils.html;
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
        view.head().title("Dummy Table");

        HtmlTable<?> t = view.body()
                .heading(1, "Dummy Table")
                .hr()
                .div()
                .table();

        HtmlTr<?> headerRow = t.tr();
        headerRow.th().text("Id1");
        headerRow.th().text("Id2");
        headerRow.th().text("Id3");
        /*
         * Act
         */
        int[][] output = {{1,2,3},{4,5,6}, {7,8,9}};
        for (int i = 0; i < output.length; i++) {
            HtmlTr<?> tr = t.tr();
            for (int j = 0; j < output[i].length; j++) {
                tr.td().text(Integer.toString(output[i][j]));
            }
        }
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        view.setPrintStream(new PrintStream(mem));
        view.write();
        /*
         * Assert
         */
        Element elem = Utils.getRootElement(mem.toByteArray());
        assertEquals(ElementType.HTML.toString(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(ElementType.HEAD.toString(), childNodes.item(1).getNodeName());
        assertEquals(ElementType.BODY.toString(), childNodes.item(3).getNodeName());
        Node div = childNodes.item(3).getChildNodes().item(5);
        assertEquals(ElementType.DIV.toString(), div.getNodeName());
        Node table = div.getChildNodes().item(1);
        assertEquals(ElementType.TABLE.toString(), table.getNodeName());
        for (int i = 0; i < output.length; i++) {
            Node tr = table.getChildNodes().item(3 + i*2);
            assertEquals(ElementType.TR.toString(), tr.getNodeName());
            for (int j = 0; j < output[i].length; j++) {
                Node td = tr.getChildNodes().item(j*2 + 1);
                assertEquals("td", td.getNodeName());
                String val = td.getFirstChild().getNodeValue();
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
        view.head().title("Dummy Table");

        HtmlTable<Iterable<Task>> t = view.body()
                .heading(1, "Dummy Table")
                .hr()
                .div()
                .table();

        HtmlTr<Iterable<Task>> headerRow = t.tr();
        headerRow.th().text("Title");
        headerRow.th().text("Description");
        headerRow.th().text("Priority");
        /*
         * Adds a dynamic Tr, which creates new Tr elements for each item
         * contained in the model received as argument of the write method.
         */
        t.trFromIterable(binderGetTitle(), binderGetDescription(), binderGetPriority());
        /*
         * Act
         */
        Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High, Status.Progress);
        Task t2 = new Task("Special dinner", "Have dinner with someone!", Priority.Normal, Status.Completed);
        Task t3 = new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High, Status.Deferred);
        List<Task> output = Arrays.asList(t1, t2, t3);
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        view.setPrintStream(new PrintStream(mem));
        view.write(output);
        /*
         * Assert
         */
        Element elem = Utils.getRootElement(mem.toByteArray());
        assertEquals(ElementType.HTML.toString(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(ElementType.HEAD.toString(), childNodes.item(1).getNodeName());
        assertEquals(ElementType.BODY.toString(), childNodes.item(3).getNodeName());
        Node div = childNodes.item(3).getChildNodes().item(5);
        assertEquals(ElementType.DIV.toString(), div.getNodeName());
        Node table = div.getChildNodes().item(1);
        assertEquals(ElementType.TABLE.toString(), table.getNodeName());
        for (int i = 0; i < output.size(); i++) {
            Node tr = table.getChildNodes().item(3 + i*2);
            assertEquals(ElementType.TR.toString(), tr.getNodeName());
            /*
             * Check title
             */
            String title = tr.getChildNodes().item(1).getFirstChild().getNodeValue();
            assertEquals(output.get(i).getTitle(), title);
            /*
             * Check task description
             */
            String desc = tr.getChildNodes().item(3).getFirstChild().getNodeValue();
            assertEquals(output.get(i).getDescription(), desc);
            /*
             * Check task priority
             */
            String prio = tr.getChildNodes().item(5).getFirstChild().getNodeValue();
            assertEquals(output.get(i).getPriority().toString(), prio);
        }
        // LOGGER.log(Level.INFO, mem.toString());
    }

    private static ModelBinder<Task, String> binderGetTitle(){
        return Task::getTitle;
    }

    private static ModelBinder<Task, String> binderGetDescription(){
        return Task::getDescription;
    }

    private static ModelBinder<Task, Priority> binderGetPriority(){
        return Task::getPriority;
    }

    @Test
    public void test_table_binding_for_readme() throws IOException, ParserConfigurationException, SAXException {
        HtmlView<Iterable<Task>>  taskView = taskListView();
        List<Task> dataSource = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        Iterator<String> actual = html(taskView, dataSource).iterator();
        Utils
                .loadLines("TaskList.html")
                .forEach(expected -> assertEquals(expected, actual.next()));

    }
    private static HtmlView<Iterable<Task>> taskListView(){
        HtmlView<Iterable<Task>> taskView = new HtmlView<>();
        taskView
                .head()
                .title("Task List")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        HtmlBody<Iterable<Task>> body = taskView.body();
        body.a("https://github.com/fmcarvalho/HtmlFlow").text("HtmlFlow");
        body.p("Html page built with HtmlFlow.");
        HtmlTable<Iterable<Task>> table = body
                .classAttr("container")
                .heading(1, "Task List")
                .hr()
                .div()
                .table().classAttr("table");
        HtmlTr<Iterable<Task>> headerRow = table.tr();
        headerRow.th().text("Title");
        headerRow.th().text("Description");
        headerRow.th().text("Priority");
        table.trFromIterable(Task::getTitle, Task::getDescription, Task::getPriority);
        return taskView;
    }
}
