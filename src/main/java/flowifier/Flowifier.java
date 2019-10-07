/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package flowifier;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

public class Flowifier {

	private final Class<? extends HtmlToFlowNodeVisitor> htmlToFlowNodeVisitorClass;
	
	public Flowifier() {
		this(HtmlToFlowNodeVisitor.class);
	}
	
	public Flowifier(final Class<? extends HtmlToFlowNodeVisitor> htmlToFlowNodeVisitorClass) {
		super();
		this.htmlToFlowNodeVisitorClass = htmlToFlowNodeVisitorClass;
	}
	
	public static class HtmlToFlowNodeVisitor implements NodeVisitor {

		private final StringBuilder builder;
		
		private boolean endOfClassFileAppended;
		
		public HtmlToFlowNodeVisitor() {
			super();
			builder = new StringBuilder();
			endOfClassFileAppended = false;
			builder.append("import htmlflow.*;\n")
			       .append("import org.xmlet.htmlapifaster.*;\n\n")
			       .append("public class Flowified {\n")
			       .append("    public static String get(){\n")
			       .append("        final String html = StaticHtml.view()\n");
		}
		
		@Override
		public void head(final Node node, final int depth) {
			if (node instanceof Document || node instanceof DocumentType) {
			} else {
				builder.append("        ");
				IntStream.range(0, depth * 4).forEach((final int index) -> builder.append(' '));
				if (node instanceof TextNode) {
					final TextNode textNode = (TextNode) node;
					builder.append(".text(\"")
					       .append(textNode.getWholeText().replace("\n", "\\n"))
					       .append("\")")
						   .append("\n");
				} else {
					builder.append(".").append(node.nodeName()).append("()");
					for (final Attribute attribute : node.attributes().asList()) {
						final String attrKey = attribute.getKey();
						final String attrVal = attribute.getValue();
						builder.append(".attr")
						       .append(attrKey.substring(0, 1).toUpperCase())
							   .append(attrKey.substring(1))
							   .append("(\"").append(attrVal)
							   .append("\")");
					}
					builder.append("\n");
				}
			}
		}

		@Override
		public void tail(final Node node, final int depth) {
			if (!(node instanceof Document) && !(node instanceof TextNode) && !(node instanceof DocumentType)) {
				builder.append("        ");
				IntStream.range(0, depth * 4).forEach((final int index) -> builder.append(' '));
			    builder.append(".__()").append(" //").append(node.nodeName()).append("\n");
			}
		}
		
		public String getFlow() {
			if (!endOfClassFileAppended) {
				builder.append("       .render();\n")
				       .append("       return html;\n")
			           .append("   }\n")
			           .append("}\n");
				endOfClassFileAppended = true;
			}
			return builder.toString();
		}
		
	} 
	
	public String toFlow(final String url) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final Document doc = Jsoup.connect(url).get();
		final HtmlToFlowNodeVisitor htmlToFlowNodeVisitor = htmlToFlowNodeVisitorClass.getConstructor().newInstance();
		doc.root().traverse(htmlToFlowNodeVisitor);
		final String result = htmlToFlowNodeVisitor.getFlow();
		return result;
	}
	
	public static void main(final String[] args) {
		try {
			System.out.println(new Flowifier().toFlow("http://tuer.sourceforge.net/en/"));
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}
}
