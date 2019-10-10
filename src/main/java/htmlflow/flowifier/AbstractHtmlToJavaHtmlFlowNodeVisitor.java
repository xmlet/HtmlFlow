package htmlflow.flowifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.xmlet.htmlapifaster.EnumContenteditableType;
import org.xmlet.htmlapifaster.EnumDirType;
import org.xmlet.htmlapifaster.EnumDraggableType;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumSpellcheckType;
import org.xmlet.htmlapifaster.EnumTranslateType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.xsdasmfaster.classes.infrastructure.EnumInterface;

public abstract class AbstractHtmlToJavaHtmlFlowNodeVisitor<T extends Appendable> implements HtmlToJavaHtmlFlowNodeVisitor<T> {

	private static final Logger LOGGER = Logger.getLogger(AbstractHtmlToJavaHtmlFlowNodeVisitor.class.getCanonicalName());
	
	private final Supplier<T> appendableSupplier;
	
	private T appendable;
	
	public AbstractHtmlToJavaHtmlFlowNodeVisitor(final Supplier<T> appendableSupplier) {
		super();
		this.appendableSupplier = Objects.requireNonNull(appendableSupplier);
	}
	
	@Override
	public final T getAppendable() {
		return appendable;
	}
	
	@Override
	public void appendHeader() throws IOException {
		appendable = appendableSupplier.get();
		appendable.append("import htmlflow.*;\n");
		appendable.append("import org.xmlet.htmlapifaster.*;\n\n");
		appendable.append("public class Flowified {\n");
		appendable.append("    public static HtmlView get(){\n");
		appendable.append("        final HtmlView html = StaticHtml.view()\n");
	}
	
	@Override
	public void appendFooter() throws IOException {
		appendable.append("            ;\n");
		appendable.append("        return html;\n");
		appendable.append("    }\n");
		appendable.append("}\n");
	}
	
	@Override
	public String convertJavaStringContentToJavaDeclarableString(final String unescaped) {
		//FIXME it doesn't escape some strings correctly, for example some JSON-LD content
		return unescaped == null ? null : "\"" + unescaped.replace("\n", "\\n").replace("\"", "\\\"") + "\"";
	}
	
	@Override
	public boolean isVoidElement(final Node node) {
		return node instanceof Document || node instanceof DataNode || node instanceof TextNode || node instanceof DocumentType;
	}
	
	@SuppressWarnings("rawtypes")
	private String toEnumAttributeValue(final Class<? extends EnumInterface<String>> enumInterfaceClass, final Attribute attribute) {
		final EnumInterface attrValEnum = Arrays.stream(enumInterfaceClass.getEnumConstants())
		      .map(EnumInterface.class::cast)
		      .filter((final EnumInterface enumInterface) -> enumInterface.getValue().equals(attribute.getValue()))
		      .findFirst()
		      .orElseThrow(() -> new IllegalArgumentException("Unknown attribute value " + attribute.getValue() + " for the enum " + enumInterfaceClass.getSimpleName()));
		return enumInterfaceClass.getSimpleName() + "." + ((Enum) attrValEnum).name();
	}
	
	@Override
	public void head(final Node node, final int depth) {
		try {
			if (depth == 0) {
				appendHeader();
			}
			if (node instanceof Document || node instanceof DocumentType) {
			} else {
				appendable.append("        ");
				for (int spaceIndex = 0; spaceIndex < depth * 4; spaceIndex++) {
					appendable.append(' ');
				}
				if (node instanceof TextNode) {
					final TextNode textNode = (TextNode) node;
					appendable.append(".text(")
							.append(convertJavaStringContentToJavaDeclarableString(textNode.getWholeText())).append(")")
							.append("\n");
				} else if (node instanceof DataNode) {
					final DataNode dataNode = (DataNode) node;
					appendable.append(".text(")
							.append(convertJavaStringContentToJavaDeclarableString(dataNode.getWholeData())).append(")")
							.append("\n");
				} else if (node instanceof Comment) {
					final Comment comment = (Comment) node;
					appendable.append(".comment(")
							.append(convertJavaStringContentToJavaDeclarableString(comment.getData())).append(")")
							.append("\n");
				} else {
					appendable.append(".").append(node.nodeName()).append("()");
					for (final Attribute attribute : node.attributes().asList()) {
						boolean rawKeyValueAttr = false;
						final String attrKey = attribute.getKey();
						// FIXME generalize, detect whether a node has the guessed method, use addAttr as a fallback
						if (attrKey.contains("-") || ("meta".equals(node.nodeName()) && "property".equals(attrKey))) {
							rawKeyValueAttr = true;
						} else {
							try {
								final String attrVal;
								switch (attrKey) {
								case "async": {
									attrVal = attribute.getKey() == null || attribute.getKey().isEmpty()
											|| attribute.getKey().equalsIgnoreCase("true") ? "Boolean.TRUE"
													: "Boolean.FALSE";
									break;
								}
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
								case "type": {
									attrVal = toEnumAttributeValue(EnumTypeContentType.class, attribute);
									break;
								}
								default: {
									attrVal = convertJavaStringContentToJavaDeclarableString(attribute.getValue());
									break;
								}
								}
								appendable.append(".attr").append(attrKey.substring(0, 1).toUpperCase(Locale.ENGLISH))
										.append(attrKey.substring(1)).append("(").append(attrVal).append(")");
							} catch (final IllegalArgumentException iae) {
								rawKeyValueAttr = true;
								//FIXME this message isn't accurate because we can arrive here just because an attribute is valid in HTML 5 but unknown by the API
								/*Logger.getLogger(this.getClass().getCanonicalName()).warning("Attribute " + attrKey
										+ " " + attribute.getValue() + " not conformant with HTML 5");*/
							}
						}
						if (rawKeyValueAttr) {
							appendable.append(".addAttr(").append("\"").append(attrKey).append("\",")
									.append(convertJavaStringContentToJavaDeclarableString(attribute.getValue()))
									.append(")");
						}
					}
					appendable.append("\n");
				}
			}
		} catch (final IOException ioe) {
			LOGGER.warning("Failed to append the Java source code, cause: " + ioe.getMessage());
		}
	}
	
	@Override
	public void tail(final Node node, final int depth) {
		try {
			if (!isVoidElement(node)) {
				appendable.append("        ");
				for (int spaceIndex = 0; spaceIndex < depth * 4; spaceIndex++) {
					appendable.append(' ');
				}
				appendable.append(".__()").append(" //").append(node.nodeName()).append("\n");
			}
			if (depth == 0) {
				appendFooter();
			}
		} catch (final IOException ioe) {
			LOGGER.warning("Failed to append the Java source code, cause: " + ioe.getMessage());
		}
	}
}
