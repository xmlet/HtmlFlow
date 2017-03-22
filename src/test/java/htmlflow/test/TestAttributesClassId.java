/*
 * Copyright (c) 2016, Mikael KROK
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    assertEquals(ElementType.DIV + " elementwas expected", ElementType.DIV.toString(), taskView.body().div().getElementName());

    String divClass = "divClass";
    String divId = "divId";
      taskView.head().scriptLink("test.css");
    taskView.body()
    .div()
      .classAttr(divClass)
      .idAttr(divId)
      .addAttr("toto", "tutu").form("/action.do");

    Task t1 = new Task("Unit Test", "Test of element name", Priority.High, Status.Progress);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(byteArrayOutputStream);
    taskView.setPrintStream(out).write(t1);
    String result = byteArrayOutputStream.toString();
    LOGGER.log(Level.INFO, result);
    assertTrue(result.contains("<div"));
    assertTrue(result.contains("</div>"));
    assertTrue(result.contains(divClass));
    assertTrue(result.contains(divId));
    assertTrue(result.contains("toto=\"tutu\""));
    assertTrue("should contains <script type=\"text/javascript\" src=\"test.css\">",
              result.contains("<script type=\"text/javascript\" src=\"test.css\">"));
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
    Task t2 = new Task("Unit Test", "Test of element name", Priority.High, Status.Progress);
    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
    PrintStream out2 = new PrintStream(byteArrayOutputStream2);
    taskView2.setPrintStream(out2).write(t2);
    LOGGER.log(Level.INFO, byteArrayOutputStream2.toString());
  }

}
