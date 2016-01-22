package htmlflow.test;

import htmlflow.HtmlView;
import htmlflow.attribute.AttributeType;
import htmlflow.elements.ElementType;
import junit.framework.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Created by mcarvalho on 18-01-2016.
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
        try(PrintStream out = new PrintStream(mem)){
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
}
