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
package htmlflow.flowifier;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;
import org.xmlet.htmlapifaster.EnumContenteditableType;
import org.xmlet.htmlapifaster.EnumDirType;
import org.xmlet.htmlapifaster.EnumDraggableType;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumSpellcheckType;
import org.xmlet.htmlapifaster.EnumTranslateType;

public class Flowifier {
	
	public Flowifier() {
		super();
	}
	
	public static class HtmlToFlowNodeVisitor implements NodeVisitor {

		private StringBuilder builder;
		
		public HtmlToFlowNodeVisitor() {
			super();
		}
		
		protected String escape(final String unescaped) {
			return unescaped == null ? null : unescaped.replace("\n", "\\n").replace("\"", "\\\"");
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected String toEnumAttributeValue(final Class<? extends Enum> enumInterfaceClass, final Attribute attribute) {
			return enumInterfaceClass.getSimpleName() + "." + Enum.valueOf(enumInterfaceClass, attribute.getValue().toUpperCase(Locale.ENGLISH)).name();
		}
		
		@Override
		public void head(final Node node, final int depth) {
			if (depth == 0) {
				builder = new StringBuilder();
				builder.append("import htmlflow.*;\n")
			       .append("import org.xmlet.htmlapifaster.*;\n\n")
			       .append("public class Flowified {\n")
			       .append("    public static HtmlView get(){\n")
			       .append("        final HtmlView html = StaticHtml.view()\n");
			}
			if (node instanceof Document || node instanceof DocumentType) {
			} else {
				builder.append("        ");
				IntStream.range(0, depth * 4).forEach((final int index) -> builder.append(' '));
				if (node instanceof TextNode) {
					final TextNode textNode = (TextNode) node;
					builder.append(".text(\"")
					       .append(escape(textNode.getWholeText()))
					       .append("\")")
						   .append("\n");
				} else if (node instanceof DataNode) {
					final DataNode dataNode = (DataNode) node;
					builder.append(".text(\"")
					       .append(escape(dataNode.getWholeData()))
					       .append("\")")
						   .append("\n");
				} else {
					builder.append(".").append(node.nodeName()).append("()");
					for (final Attribute attribute : node.attributes().asList()) {
						final String attrKey = attribute.getKey();
						try {
							final String attrVal;
							switch (attrKey) {
							case "contenteditable": {
								attrVal = toEnumAttributeValue(EnumContenteditableType.class, attribute);
								break;
							}
							case "dir": {
								attrVal = toEnumAttributeValue(EnumDirType.class, attribute);
								break;
							}
							case "draggable": {
								attrVal = toEnumAttributeValue(EnumDraggableType.class, attribute);
								break;
							}
							case "rel": {
								attrVal = toEnumAttributeValue(EnumRelType.class, attribute);
								break;
							}
							case "spellcheck": {
								attrVal = toEnumAttributeValue(EnumSpellcheckType.class, attribute);
								break;
							}
							case "translate": {
								attrVal = toEnumAttributeValue(EnumTranslateType.class, attribute);
								break;
							}
							default: {
								attrVal = "\"" + escape(attribute.getValue()) + "\"";
								break;
							}
							}
							builder.append(".attr")
							       .append(attrKey.substring(0, 1).toUpperCase(Locale.ENGLISH))
								   .append(attrKey.substring(1))
								   .append("(")
								   .append(attrVal)
								   .append(")");
						} catch (final IllegalArgumentException iae) {
							Logger.getLogger(this.getClass().getCanonicalName())
									.warning("Attribute " + attribute.getKey() + " " + attribute.getValue() + " skipped because it is not conformant with HTML 5");
						}
					}
					builder.append("\n");
				}
			}
		}

		@Override
		public void tail(final Node node, final int depth) {
			if (!(node instanceof Document) && !(node instanceof DataNode) && !(node instanceof TextNode) && !(node instanceof DocumentType)) {
				builder.append("        ");
				IntStream.range(0, depth * 4).forEach((final int index) -> builder.append(' '));
			    builder.append(".__()").append(" //").append(node.nodeName()).append("\n");
			}
			if (depth == 0) {
				builder.append("            ;\n")
				       .append("        return html;\n")
			           .append("    }\n")
			           .append("}\n");
			}
		}
		
		public String getFlow() {
			return builder == null ? null : builder.toString();
		}
		
	} 
	
	public String toFlow(final Node node, final HtmlToFlowNodeVisitor htmlToFlowNodeVisitor) {
		node.traverse(htmlToFlowNodeVisitor);
		final String result = htmlToFlowNodeVisitor.getFlow();
		return result;
	}
	
	public String toFlow(final Document doc, final HtmlToFlowNodeVisitor htmlToFlowNodeVisitor) {
		return toFlow(doc.root(), htmlToFlowNodeVisitor);
	}
	
	public String toFlow(final Document doc) {
		return toFlow(doc, new HtmlToFlowNodeVisitor());
	}
	
	public String toFlow(final String url, final HtmlToFlowNodeVisitor htmlToFlowNodeVisitor) throws IOException {
		final Document doc = Jsoup.connect(url).get();
		return toFlow(doc, htmlToFlowNodeVisitor);
	}
	
	public String toFlow(final String url) throws IOException {
		final Document doc = Jsoup.connect(url).get();
		return toFlow(doc, new HtmlToFlowNodeVisitor());
	}
}
