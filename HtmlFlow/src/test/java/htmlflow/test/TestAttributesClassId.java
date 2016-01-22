/**
 * 
 */
package htmlflow.test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import htmlflow.elements.ElementType;
import org.junit.Assert;
import org.junit.Test;

import htmlflow.HtmlView;
import htmlflow.ModelBinder;
import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;

/**
 * @author Mikael KROK
 *
 */
public class TestAttributesClassId {

  private static ModelBinder<Task> binderGetId() {
    return new ModelBinder<Task>() {
      public void bind(PrintStream out, Task model) {
        out.print(model.getId());
      }
    };
  }

  private static ModelBinder<Task> binderGetTitle() {
    return new ModelBinder<Task>() {
      public void bind(PrintStream out, Task model) {
        out.print(model.getTitle());
      }
    };
  }

  private static ModelBinder<Task> binderGetDescription() {
    return new ModelBinder<Task>() {
      public void bind(PrintStream out, Task model) {
        out.print(model.getDescription());
      }
    };
  }

  private static ModelBinder<Task> binderGetPriority() {
    return new ModelBinder<Task>() {
      public void bind(PrintStream out, Task model) {
        out.print(model.getPriority());
      }
    };
  }

  /**
   * Test method for {@link htmlflow.elements.HtmlDiv#getElementName()}.
   */
  @Test
  public void testGetElementName() throws Exception {
    HtmlView<Task> taskView = new HtmlView<Task>();
    Assert.assertEquals(ElementType.DIV + " element was expected", ElementType.DIV.toString(), taskView.body().div().getElementName());
  }

  @Test
  public void testIdAndClassAttribute() throws Exception {
    HtmlView<Task> taskView = new HtmlView<Task>();
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
    System.out.println(result);
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
  public void testDoWrite() throws Exception {

    HtmlView<Task> taskView2 = new HtmlView<Task>();

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
    System.out.println(byteArrayOutputStream2.toString());
  }

}
