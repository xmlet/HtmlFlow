package htmlflow.test;

import htmlflow.HtmlView;
import htmlflow.ModelBinder;
import htmlflow.elements.HtmlTable;
import htmlflow.elements.HtmlTr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestTable {

	private static final String NODE_NAME_TR = "tr";
	private static final String NODE_NAME_TABLE = "table";
	private static final String NODE_NAME_DIV = "div";
	private static final String NODE_NAME_BODY = "body";
	private static final String NODE_NAME_HEAD = "head";
	private static final String NODE_NAME_HTML = "html";

	private static Element getRootElement(byte[] input) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(input));
		return doc.getDocumentElement();
	}

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
		Element elem = getRootElement(mem.toByteArray());
		Assert.assertEquals(NODE_NAME_HTML, elem.getNodeName());
		NodeList childNodes = elem.getChildNodes();
		Assert.assertEquals(NODE_NAME_HEAD, childNodes.item(1).getNodeName());
		Assert.assertEquals(NODE_NAME_BODY, childNodes.item(3).getNodeName());
		Node div = childNodes.item(3).getChildNodes().item(5);
		Assert.assertEquals(NODE_NAME_DIV, div.getNodeName());
		Node table = div.getChildNodes().item(1);
		Assert.assertEquals(NODE_NAME_TABLE, table.getNodeName());
		for (int i = 0; i < output.length; i++) {
			Node tr = table.getChildNodes().item(3 + i*2);
			Assert.assertEquals(NODE_NAME_TR, tr.getNodeName());
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
		Element elem = getRootElement(mem.toByteArray());
		Assert.assertEquals(NODE_NAME_HTML, elem.getNodeName());
		NodeList childNodes = elem.getChildNodes();
		Assert.assertEquals(NODE_NAME_HEAD, childNodes.item(1).getNodeName());
		Assert.assertEquals(NODE_NAME_BODY, childNodes.item(3).getNodeName());
		Node div = childNodes.item(3).getChildNodes().item(5);
		Assert.assertEquals(NODE_NAME_DIV, div.getNodeName());
		Node table = div.getChildNodes().item(1);
		Assert.assertEquals(NODE_NAME_TABLE, table.getNodeName());
		for (int i = 0; i < output.size(); i++) {
			Node tr = table.getChildNodes().item(3 + i*2);
			Assert.assertEquals(NODE_NAME_TR, tr.getNodeName());
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
	
	private static ModelBinder<Task> binderGetTitle(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getTitle());
		}};
	}
	
	private static ModelBinder<Task> binderGetDescription(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getDescription());
		}};
	}
	
	private static ModelBinder<Task> binderGetPriority(){
		return new ModelBinder<Task>() {public void bind(PrintStream out, Task model) {
			out.print(model.getPriority());
		}};
	}
}
