package htmlflow.flowifier;

public class DefaultHtmlToJavaHtmlFlowNodeVisitor extends AbstractHtmlToJavaHtmlFlowNodeVisitor<StringBuilder> {

	public DefaultHtmlToJavaHtmlFlowNodeVisitor() {
		super(StringBuilder::new);
	}
}
