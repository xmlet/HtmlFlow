package htmlflow.test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by mcarvalho on 22-01-2016.
 */
public class DomUtils {
	static Element getRootElement(byte[] input) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(input));
		return doc.getDocumentElement();
	}
}
