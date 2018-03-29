/*
 * MIT License
 *
 * Copyright (c) 2015-16, Mikael KROK
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
/**
 * 
 */
package htmlflow.test;

import htmlflow.HtmlView;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlapi.BaseAttribute;
import org.xmlet.htmlapi.Div;
import org.xmlet.htmlapi.EnumEnctypeForm;
import org.xmlet.htmlapi.EnumMethodForm;
import org.xmlet.htmlapi.EnumTypeScript;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static htmlflow.test.Utils.html;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Mikael KROK
 *
 */
public class TestAttributesClassId {

  private static final Logger LOGGER = Logger.getLogger("htmlflow.test");
  private static final String DIV_NAME = Div.class.getSimpleName().toLowerCase();

  @Test
  public void testGetElementName() {
    HtmlView<Task> taskView = new HtmlView<>();
    Assert.assertEquals(DIV_NAME + " element was expected", DIV_NAME, taskView.body().div().getName());
  }

  @Test
  public void testIdAndClassAttribute() {
    HtmlView<Task> taskView = new HtmlView<>();
    assertEquals(
            DIV_NAME + " element was expected",
            DIV_NAME,
            taskView.body().div().getName());

    String divClass = "divClass";
    String divId = "divId";
    taskView
            .head()
            .script()
            .attrType(EnumTypeScript.TEXT_JAVASCRIPT)
            .attrSrc("test.css");
    taskView.body()
            .div()
            .attrId(divId)
            .attrClass(divClass)
            .addAttr(new BaseAttribute("tutu", "toto"))
            .form()
            .attrAction("/action.do")
            .attrMethod(EnumMethodForm.POST)
            .attrEnctype(EnumEnctypeForm.APPLICATION_X_WWW_FORM_URLENCODED);

    Task t1 = new Task("Unit Test", "Test of element name", Priority.High, Status.Progress);
    List<String> actual = html(taskView, t1).collect(toList());

    String result = actual.stream().collect(joining("\n"));
    //System.out.println(result);
    assertTrue(result.contains("<div"));
    assertTrue(result.contains("</div>"));
    assertTrue(result.contains(divClass));
    assertTrue(result.contains(divId));
    assertTrue(result.contains("toto=\"tutu\""));
    assertTrue("should contains <script type=\"text/javascript\" src=\"test.css\">",
              result.contains("<script type=\"text/javascript\" src=\"test.css\">"));

    Iterator<String> iter = actual.iterator();
    Utils
            .loadLines("testIdAndClassAttribute.html")
            .forEach(expected -> assertEquals(expected, iter.next()));
  }
  
  @Test
  public void testDoWrite() {

    HtmlView<Task> taskView2 = new HtmlView<>();

    taskView2.head().title().text("Task Details");
    taskView2.body()
            .h1().text("Task Details").º()
            .hr().º()
            .div().text("Id:").text(Task::getId)
            .br().º()
            .text("Title:").text(Task::getTitle)
            .br().º()
            .text("Description:").text(Task::getDescription)
            .br().º()
            .text("Priority:").text(Task::getPriority);
    Task t2 = new Task(5243, "Unit Test", "Test of element name", Priority.High, Status.Progress);

    List<String> actual = html(taskView2, t2).collect(toList());
    actual.forEach(System.out::println);
    Iterator<String> iter = actual.iterator();
    Utils
            .loadLines("testDoWrite.html")
            .forEach(expected -> assertEquals(expected, iter.next()));
  }

}
