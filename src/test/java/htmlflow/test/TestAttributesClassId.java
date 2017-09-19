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
import htmlflow.ModelBinder;
import htmlflow.elements.ElementType;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

  private static ModelBinder<Task, Integer> binderGetId() {
    return Task::getId;
  }

  private static ModelBinder<Task, String> binderGetTitle() {
    return Task::getTitle;
  }

  private static ModelBinder<Task, String> binderGetDescription() {
    return Task::getDescription;
  }

  private static ModelBinder<Task, Priority> binderGetPriority() {
    return Task::getPriority;
  }

  /**
   * Test method for {@link htmlflow.elements.HtmlDiv#getElementName()}.
   */
  @Test
  public void testGetElementName() {
    HtmlView<Task> taskView = new HtmlView<>();
    Assert.assertEquals(ElementType.DIV + " element was expected", ElementType.DIV.toString(), taskView.body().div().getElementName());
  }

  @Test
  public void testIdAndClassAttribute() {
    HtmlView<Task> taskView = new HtmlView<>();
    assertEquals(
            ElementType.DIV + " elementwas expected",
            ElementType.DIV.toString(),
            taskView.body().div().getElementName());

    String divClass = "divClass";
    String divId = "divId";
      taskView.head().scriptLink("test.css");
    taskView.body()
    .div()
      .classAttr(divClass)
      .idAttr(divId)
      .addAttr("toto", "tutu").form("/action.do");

    Task t1 = new Task("Unit Test", "Test of element name", Priority.High, Status.Progress);
    List<String> actual = html(taskView, t1).collect(toList());

    String result = actual.stream().collect(joining());
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
  
  /**
   * Test method for
   * {@link htmlflow.HtmlWriterComposite#doWriteBefore(java.io.PrintStream, int)}
   * .
   */
  @Test
  public void testDoWrite() {

    HtmlView<Task> taskView2 = new HtmlView<>();

    taskView2.head().title("Task Details");
    taskView2.body()
    .heading(0, "Task Details")
    .hr()
    .div().text("Id: ").text(binderGetId())
    .br().text("Title: ")
    .text(binderGetTitle()).br().text("Description: ").text(binderGetDescription()).br().text("Priority: ")
        .text(binderGetPriority());
    Task t2 = new Task(5243, "Unit Test", "Test of element name", Priority.High, Status.Progress);

    Iterator<String> actual = html(taskView2, t2).iterator();
    Utils
            .loadLines("testDoWrite.html")
            .forEach(expected -> assertEquals(expected, actual.next()));
  }

}
