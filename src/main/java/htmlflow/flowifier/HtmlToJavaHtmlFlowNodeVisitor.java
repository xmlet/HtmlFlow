package htmlflow.flowifier;

import java.io.IOException;

import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

/**
 * Visitor of a JSoup node that converts the HTML source code into a Java class
 * 
 * @author Julien Gouesse
 *
 * @param <T> the type of appendable used to store the Java source code
 */
public interface HtmlToJavaHtmlFlowNodeVisitor<T extends Appendable> extends NodeVisitor {

	/**
	 * Appends the header of the Java class, i.e the imports, the declaration of the class, 
	 * the declaration of the method, ...
	 * 
	 * @throws IOException thrown when something wrong occurs while appending the Java source code
	 */
	void appendHeader() throws IOException;
	/**
	 * Appends the footer of the Java class, i.e closes the method and the class
	 * 
	 * @throws IOException thrown when something wrong occurs while appending the Java source code
	 */
	void appendFooter() throws IOException;
	/**
	 * Tells whether a JSoup node cannot be closed
	 * 
	 * @param node the JSoup node
	 * @return <code>true</code> if the JSoup node cannot be closed, otherwise <code>false</code>
	 */
	boolean isUncloseable(Node node);
	/**
	 * Converts the content of a Java string into a string that can be declared in a Java class passed as a method parameter
	 * 
	 * @param javaStringContent the content of a Java string
	 * @return a string that can be declared in a Java class passed as a method parameter
	 */
	String convertJavaStringContentToJavaDeclarableString(String javaStringContent);
	/**
	 * Returns the appendable used to store the Java class
	 * 
	 * @return the appendable used to store the Java class
	 */
	T getAppendable();
}
