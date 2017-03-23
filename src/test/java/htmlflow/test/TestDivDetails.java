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
import htmlflow.HtmlWriter;
import htmlflow.attribute.AttributeType;
import htmlflow.elements.ElementType;
import htmlflow.test.model.Priority;
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

/**
 * @author Miguel Gamboa
 * Created on 18-01-2016.
 */
public class TestDivDetails {
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
        Element elem = DomUtils.getRootElement(mem.toByteArray());
        Assert.assertEquals(ElementType.HTML.toString(), elem.getNodeName());
        NodeList childNodes = elem.getChildNodes();
        Node head = childNodes.item(1);
        Assert.assertEquals(ElementType.HEAD.toString(), head.getNodeName());
        Node body = childNodes.item(3);
        Assert.assertEquals(ElementType.BODY.toString(), body.getNodeName());
        Node bodyClassAttr = body.getAttributes().getNamedItem(AttributeType.CLASS.toString());
        Assert.assertEquals("container", bodyClassAttr.getNodeValue());
        /*
         * Assert HTML Head
         */
        childNodes = head.getChildNodes();
        Assert.assertEquals(ElementType.TITLE.toString(), childNodes.item(1).getNodeName());
        Assert.assertEquals(ElementType.LINK.toString(), childNodes.item(3).getNodeName());
    }

    @Test
    public void test_div_details_binding() throws IOException, ParserConfigurationException, SAXException {
        HtmlView<Task> taskView = taskDetailsView();
        Task [] dataSource = {
                new Task("ISEL MPD project", "A Java library for serializing objects in HTML.", Priority.High),
                new Task("Special dinner", "Have dinner with someone!", Priority.Normal),
                new Task("Manchester City - Sporting", "1/8 Final UEFA Europa League. VS. Manchester City - Sporting!", Priority.High)
        };
        Arrays
                .stream(dataSource)
                .forEach(task -> printHtml(taskView, task, "task" + task.getId() + ".html"));
    }
    private static <T> void printHtml(HtmlWriter<T> html, T model, String path){
        try(PrintStream out = new PrintStream(new FileOutputStream(path))){
            html.setPrintStream(out).write(model);
            // Runtime.getRuntime().exec("explorer Task.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}