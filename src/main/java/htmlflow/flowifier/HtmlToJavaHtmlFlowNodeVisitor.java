package htmlflow.flowifier;

import java.io.IOException;

import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public interface HtmlToJavaHtmlFlowNodeVisitor<T extends Appendable> extends NodeVisitor {

	void appendHeader() throws IOException;
	void appendFooter() throws IOException;
	boolean isVoidElement(Node node);
	String convertJavaStringContentToJavaDeclarableString(String javaStringContent);
	T getAppendable();
}
