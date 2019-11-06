package htmlflow.flowifier;

/**
 * Default implementation of the visitor of a JSoup node that converts the HTML source code into a Java class, it uses a StringBuilder to append the 
 * content of the Java class. Note that this implementation is not intended to be thread-safe
 * 
 * @author Julien Gouesse
 *
 */
public class DefaultHtmlToJavaHtmlFlowNodeVisitor extends AbstractHtmlToJavaHtmlFlowNodeVisitor<StringBuilder> {

	/**
	 * Constructor with indentation disabled
	 */
	public DefaultHtmlToJavaHtmlFlowNodeVisitor() {
		this(false);
	}
	
	/**
	 * Constructor
	 * 
	 * @param indented <code>true</code> if the generated HTML source code is indented, otherwise <code>false</code>
	 */
	public DefaultHtmlToJavaHtmlFlowNodeVisitor(final boolean indented) {
		super(StringBuilder::new, indented);
	}
}
