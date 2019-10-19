package htmlflow.flowifier;

/**
 * Default implementation of the visitor of a JSoup node that converts the HTML source code into a Java class, it uses a StringBuilder to append the 
 * content of the Java class. Note that this implementation is not intended to be thread-safe
 * 
 * @author Julien Gouesse
 *
 */
public class DefaultHtmlToJavaHtmlFlowNodeVisitor extends AbstractHtmlToJavaHtmlFlowNodeVisitor<StringBuilder> {

	public DefaultHtmlToJavaHtmlFlowNodeVisitor() {
		super(StringBuilder::new);
	}
}
