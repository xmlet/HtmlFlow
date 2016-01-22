package htmlflow.test;

import htmlflow.HtmlView;
import htmlflow.ModelBinder;
import htmlflow.elements.ElementType;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import htmlflow.test.model.Priority;
import htmlflow.test.model.Status;
import htmlflow.test.model.Task;
import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestTable {

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
				tr.td().text(output[i][j] + "");
			}
		}
		ByteArrayOutputStream mem = new ByteArrayOutputStream();
		view.setPrintStream(new PrintStream(mem));
		view.write();
		/*
		 * Assert
		 */
		Element elem = DomUtils.getRootElement(mem.toByteArray());
		Assert.assertEquals(ElementType.HTML.toString(), elem.getNodeName());
		NodeList childNodes = elem.getChildNodes();
		Assert.assertEquals(ElementType.HEAD.toString(), childNodes.item(1).getNodeName());
		Assert.assertEquals(ElementType.BODY.toString(), childNodes.item(3).getNodeName());
		Node div = childNodes.item(3).getChildNodes().item(5);
		Assert.assertEquals(ElementType.DIV.toString(), div.getNodeName());
		Node table = div.getChildNodes().item(1);
		Assert.assertEquals(ElementType.TABLE.toString(), table.getNodeName());
		for (int i = 0; i < output.length; i++) {
			Node tr = table.getChildNodes().item(3 + i*2);
			Assert.assertEquals(ElementType.TR.toString(), tr.getNodeName());
			for (int j = 0; j < output[i].length; j++) {
				Node td = tr.getChildNodes().item(j*2 + 1);
				Assert.assertEquals("td", td.getNodeName());
				String val = td.getFirstChild().getNodeValue();
				Assert.assertEquals(output[i][j], Integer.parseInt(val));
			}
		}
		System.out.println(mem.toString());
	}
	@Test
	public void test_table_with_binding() throws ParserConfigurationException, SAXException, IOException{
		/*
		 * Arrange
		 */
		HtmlView<Iterable<Task>> view = new HtmlView<Iterable<Task>>();
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
		Element elem = DomUtils.getRootElement(mem.toByteArray());
		Assert.assertEquals(ElementType.HTML.toString(), elem.getNodeName());
		NodeList childNodes = elem.getChildNodes();
		Assert.assertEquals(ElementType.HEAD.toString(), childNodes.item(1).getNodeName());
		Assert.assertEquals(ElementType.BODY.toString(), childNodes.item(3).getNodeName());
		Node div = childNodes.item(3).getChildNodes().item(5);
		Assert.assertEquals(ElementType.DIV.toString(), div.getNodeName());
		Node table = div.getChildNodes().item(1);
		Assert.assertEquals(ElementType.TABLE.toString(), table.getNodeName());
		for (int i = 0; i < output.size(); i++) {
			Node tr = table.getChildNodes().item(3 + i*2);
			Assert.assertEquals(ElementType.TR.toString(), tr.getNodeName());
			/*
			 * Check title
			 */
			String title = tr.getChildNodes().item(1).getFirstChild().getNodeValue();
			Assert.assertEquals(output.get(i).getTitle(), title);
			/*
			 * Check task description
			 */
			String desc = tr.getChildNodes().item(3).getFirstChild().getNodeValue();
			Assert.assertEquals(output.get(i).getDescription(), desc);
			/*
			 * Check task priority
			 */
			String prio = tr.getChildNodes().item(5).getFirstChild().getNodeValue();
			Assert.assertEquals(output.get(i).getPriority().toString(), prio);
		}        
		System.out.println(mem.toString());
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
		try(PrintStream out = new PrintStream(new FileOutputStream("TaskList.html"))){
			taskView.setPrintStream(out).write(dataSource);
			// Runtime.getRuntime().exec("explorer TaskList.html");
		}
	}
	private static HtmlView<Iterable<Task>> taskListView(){
		HtmlView<Iterable<Task>> taskView = new HtmlView<Iterable<Task>>();
		taskView
				.head()
				.title("Task List")
				.linkCss("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css");
		HtmlTable<Iterable<Task>> table = taskView
				.body().classAttr("container")
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
