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
import htmlflow.attribute.AttributeType;
import htmlflow.elements.ElementType;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Task;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static htmlflow.test.Utils.html;
import static htmlflow.test.Utils.loadLines;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static junit.framework.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 * Created on 18-01-2016.
 */
public class TestDivDetails {

    private final Map<Task, Stream<String>> expectedTaskViews;

    public TestDivDetails() {
        this.expectedTaskViews =
                Stream.of(
                        new Task(3, "ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                        new Task(4, "Special dinner", "Have dinner with someone!", Priority.Normal),
                        new Task(5, "Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
                ).collect(Collectors.toMap(
                        identity(),
                        task -> loadLines(format("task%d.html", task.getId()))
                ));
    }

    @Test
    public void test_div_details_without_binding() throws IOException, ParserConfigurationException, SAXException {
        HtmlView<?> taskView = new HtmlView<>();
        taskView
                .head()
                .title("Task Details")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
                .body().classAttr("container")
                .heading(1, "Task Details")
                .hr()
                .div()
                .text("Title: ").text("ISEL MPD project")
                .br()
                .text("Description: ").text("A Java library for serializing objects in HTML.")
                .br()
                .text("Priority: ").text("HIGH");
        //
        // Produces an HTML file document
        //
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(mem)) {
            taskView.setPrintStream(out).write();
            // System.out.println(mem.toString());
        }
        /*
         * Assert HTML document main structure
         */
        Element elem = Utils.getRootElement(mem.toByteArray());
        assertEquals(ElementType.HTML.toString(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        Node head = childNodes.item(1);
        assertEquals(ElementType.HEAD.toString(), head.getNodeName());
        Node body = childNodes.item(3);
        assertEquals(ElementType.BODY.toString(), body.getNodeName());
        Node bodyClassAttr = body.getAttributes().getNamedItem(AttributeType.CLASS.toString());
        assertEquals("container", bodyClassAttr.getNodeValue());
        /*
         * Assert HTML Head
         */
        childNodes = head.getChildNodes();
        assertEquals(ElementType.TITLE.toString(), childNodes.item(1).getNodeName());
        assertEquals(ElementType.LINK.toString(), childNodes.item(3).getNodeName());
    }

    @Test
    public void test_div_details_binding() throws IOException, ParserConfigurationException, SAXException {
        expectedTaskViews
                .keySet()
                .stream()
                .map(task -> TaskHtml.of(task, html(taskDetailsView(), task)))
                .forEach(taskHtml -> {
                    Iterator<String> actual = taskHtml.html.iterator();
                    expectedTaskViews
                            .get(taskHtml.t)
                            .forEach(line -> assertEquals(line, actual.next()));
                });
    }


    private static HtmlView<Task> taskDetailsView(){
        HtmlView<Task> taskView = new HtmlView<>();
        taskView
                .head()
                .title("Task Details")
                .linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
        taskView
                .body().classAttr("container")
                .heading(1, "Task Details")
                .hr()
                .div()
                .text("Title: ").text(Task::getTitle)
                .br()
                .text("Description: ").text(Task::getDescription)
                .br()
                .text("Priority: ").text(Task::getPriority);
        return taskView;
    }
    private static class TaskHtml {
        Task t;
        Stream<String> html;
        public TaskHtml(Task t, Stream<String> html) {
            this.t = t;
            this.html = html;
        }
        static TaskHtml of(Task t, Stream<String> html) {
            return new TaskHtml(t, html);
        }
    }
}