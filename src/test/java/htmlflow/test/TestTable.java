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

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import htmlflow.test.views.HtmlTables;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Head;
import org.xmlet.htmlapifaster.Html;
import org.xmlet.htmlapifaster.Table;
import org.xmlet.htmlapifaster.Tr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 */
public class TestTable {

    private static final Logger LOGGER = Logger.getLogger("htmlflow.test");
    private static final Pattern NEWLINE = Pattern.compile("\n");

    @Test
    public void testSimpleTableRender() throws IOException{
        /*
         * Arrange
         */
        int[][] output = {{1,2,3},{4,5,6}, {7,8,9}};
        HtmlView view = HtmlFlow.view(HtmlTables::simpleTableView);
        /*
         * Act
         */
        String html = view.render(output);
        /*
         * Assert
         */
        assertSimpleHtmlView(html.getBytes(), output);
    }

    @Test
    public void testSimpleTableWrite() throws IOException{
        /*
         * Arrange and Act
         */
        int[][] output = {{1,2,3},{4,5,6}, {7,8,9}};
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        HtmlFlow
            .view(new PrintStream(mem), HtmlTables::simpleTableView)
            .write(output);
        /*
         * Assert
         */
        assertSimpleHtmlView(mem.toByteArray(), output);
    }

    @Test
    public void testNestedTable(){
        /*
         * Arrange
         */
        Iterator<String> expected = Utils.loadLines("nestedTable.html").iterator();
        /*
         * Act
         */
        String html = HtmlTables.nestedTable.render();
        /*
         * Assert
         */
        NEWLINE
            .splitAsStream(html)
            .forEach(actual -> assertEquals(expected.next(), actual));
    }

    @Test
    public void testTableWithBinding() throws IOException{
        /*
         * Act
         */
        Task t1 = new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High, Status.Progress);
        Task t2 = new Task("Special dinner", "Have dinner with someone!", Priority.Normal, Status.Completed);
        Task t3 = new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High, Status.Deferred);
        List<Task> tasks = Arrays.asList(t1, t2, t3);
        String html = HtmlFlow.view(HtmlTables::taskTableView).render(tasks);
        /*
         * Assert
         */
        assertTaskHtmlView(html.getBytes(), tasks);
    }

    @Test
    public void testTableWithPartialsBindingTwiceToPrintStream() {
        List<Task> dataSource = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        HtmlView view = HtmlFlow.view(
            new PrintStream(mem),
            HtmlTables.taskListViewWithPartials(HtmlTables::taskListRow));
        view.write(dataSource);
        validateBindingTable(mem.toString());
        mem.reset();
        view.write(dataSource);
        validateBindingTable(mem.toString());
    }


    @Test
    public void testTableWithPartialsBindingTwice() {
        List<Task> dataSource = Arrays.asList(
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        );
        HtmlView view = HtmlFlow.view(HtmlTables.taskListViewWithPartials(HtmlTables::taskListRow));
        validateBindingTable(view.render(dataSource));
        validateBindingTable(view.render(dataSource));
    }

    static void validateBindingTable(String actual){
        Iterator<String> iter = NEWLINE
            .splitAsStream(actual)
            .iterator();
        Utils
                .loadLines("TaskList.html")
                .forEach(expected -> {
                    String line = iter.next();
                    // System.out.println(line);
                    assertEquals(expected, line);
                });

    }

    private static void assertTaskHtmlView(byte[] html, List<Task> output) throws UnsupportedEncodingException {
        org.w3c.dom.Element elem = Utils.getRootElement(html);
        assertEquals(Html.class.getSimpleName().toLowerCase(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(Head.class.getSimpleName().toLowerCase(), childNodes.item(1).getNodeName());
        assertEquals(Body.class.getSimpleName().toLowerCase(), childNodes.item(3).getNodeName());
        Node div = childNodes.item(3).getChildNodes().item(5);
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

    private static void assertSimpleHtmlView(byte[] html, int[][] output) throws UnsupportedEncodingException {
        org.w3c.dom.Element elem = Utils.getRootElement(html);
        assertEquals(Html.class.getSimpleName().toLowerCase(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        assertEquals(Head.class.getSimpleName().toLowerCase(), childNodes.item(1).getNodeName());
        assertEquals(Body.class.getSimpleName().toLowerCase(), childNodes.item(3).getNodeName());
        Node div = childNodes.item(3).getChildNodes().item(5);
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
