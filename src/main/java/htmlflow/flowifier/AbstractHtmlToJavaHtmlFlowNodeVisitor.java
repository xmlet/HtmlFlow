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
import org.xmlet.htmlapifaster.EnumAutocompleteType;
import org.xmlet.htmlapifaster.EnumBorderType;
import org.xmlet.htmlapifaster.EnumContenteditableType;
import org.xmlet.htmlapifaster.EnumCrossoriginCrossOriginType;
import org.xmlet.htmlapifaster.EnumDirType;
import org.xmlet.htmlapifaster.EnumDisplayType;
import org.xmlet.htmlapifaster.EnumDraggableType;
import org.xmlet.htmlapifaster.EnumEnctypeType;
import org.xmlet.htmlapifaster.EnumFormenctypeEnctypeType;
import org.xmlet.htmlapifaster.EnumFormmethodMethodType;
import org.xmlet.htmlapifaster.EnumFormtargetBrowsingContext;
import org.xmlet.htmlapifaster.EnumHttpEquivType;
import org.xmlet.htmlapifaster.EnumKindType;
import org.xmlet.htmlapifaster.EnumMediaType;
import org.xmlet.htmlapifaster.EnumMethodType;
import org.xmlet.htmlapifaster.EnumOverflowType;
import org.xmlet.htmlapifaster.EnumRelType;
import org.xmlet.htmlapifaster.EnumRevType;
import org.xmlet.htmlapifaster.EnumSandboxType;
import org.xmlet.htmlapifaster.EnumScopeType;
import org.xmlet.htmlapifaster.EnumShapeType;
import org.xmlet.htmlapifaster.EnumSpellcheckType;
import org.xmlet.htmlapifaster.EnumTranslateType;
import org.xmlet.htmlapifaster.EnumTypeButtonType;
import org.xmlet.htmlapifaster.EnumTypeContentType;
import org.xmlet.htmlapifaster.EnumTypeInputType;
import org.xmlet.htmlapifaster.EnumTypeOlType;
import org.xmlet.htmlapifaster.EnumTypeScriptType;
import org.xmlet.htmlapifaster.EnumTypeSimpleContentType;
import org.xmlet.htmlapifaster.EnumWrapType;
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
	
	/**
	 * This method comes from Javapoet
	 * 
	 * @param c
	 * @return
	 */
	private String characterLiteralWithoutSingleQuotes(final char c) {
	    // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
	    switch (c) {
	      case '\b': return "\\b"; /* \u0008: backspace (BS) */
	      case '\t': return "\\t"; /* \u0009: horizontal tab (HT) */
	      case '\n': return "\\n"; /* \u000a: linefeed (LF) */
	      case '\f': return "\\f"; /* \u000c: form feed (FF) */
	      case '\r': return "\\r"; /* \u000d: carriage return (CR) */
	      case '\"': return "\"";  /* \u0022: double quote (") */
	      case '\'': return "\\'"; /* \u0027: single quote (') */
	      case '\\': return "\\\\";  /* \u005c: backslash (\) */
	      default:
	        return Character.isISOControl(c) ? String.format("\\u%04x", (int) c) : Character.toString(c);
	    }
	  }

	  /** Returns the string literal representing {@code value}, including wrapping double quotes. 
	   * 
	   * This method comes from Javapoet
	   * */
	  private String stringLiteralWithDoubleQuotes(final String value, final String indent) {
	    StringBuilder result = new StringBuilder(value.length() + 2);
	    result.append('"');
	    for (int i = 0; i < value.length(); i++) {
	      char c = value.charAt(i);
	      // trivial case: single quote must not be escaped
	      if (c == '\'') {
	        result.append("'");
	        continue;
	      }
	      // trivial case: double quotes must be escaped
	      if (c == '\"') {
	        result.append("\\\"");
	        continue;
	      }
	      // default case: just let character literal do its work
	      result.append(characterLiteralWithoutSingleQuotes(c));
	      // need to append indent after linefeed?
	      if (c == '\n' && i + 1 < value.length()) {
	        result.append("\"\n").append(indent).append(indent).append("+ \"");
	      }
	    }
	    result.append('"');
	    return result.toString();
	  }
	
	@Override
	public String convertJavaStringContentToJavaDeclarableString(final String unescaped) {
		return unescaped == null ? null : stringLiteralWithDoubleQuotes(unescaped, "");
	}
	
	@Override
	public boolean isUnclosable(final Node node) {
		return node instanceof Document || node instanceof DataNode || node instanceof Comment || node instanceof TextNode || node instanceof DocumentType;
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
					final String nodeClassname = node.nodeName().substring(0, 1).toUpperCase(Locale.ENGLISH) + node.nodeName().substring(1).toLowerCase(Locale.ENGLISH);
					Class<?> nodeClass = null;
					try {
					    nodeClass = Class.forName("org.xmlet.htmlapifaster." + nodeClassname);
					} catch (final ClassNotFoundException cnfe) {
						LOGGER.severe("Class not found " + nodeClassname);
					}
					if (nodeClass != null) {
					    for (final Attribute attribute : node.attributes().asList()) {
							final String attrKey = attribute.getKey();
							final String attrMethodname = "attr" + attrKey.substring(0, 1).toUpperCase(Locale.ENGLISH) + attrKey.substring(1);
							String attrVal = null;
							try {
								nodeClass.getMethod(attrMethodname, String.class);
								attrVal = convertJavaStringContentToJavaDeclarableString(attribute.getValue());
							} catch (final NoSuchMethodException nsme) {
								try {
								nodeClass.getMethod(attrMethodname, Boolean.class);
								attrVal = attrKey.isEmpty() || attrKey.equalsIgnoreCase("true") ? "Boolean.TRUE" : "Boolean.FALSE";
							} catch (final NoSuchMethodException nsme1) {
								try {
								nodeClass.getMethod(attrMethodname, Long.class);
								attrVal = "Long.valueOf(" + attribute.getValue() + "L)";
								} catch (final NoSuchMethodException nsme2) {
									try {
										nodeClass.getMethod(attrMethodname, Integer.class);
										attrVal = "Integer.valueOf(" + attribute.getValue() + ")";
								    } catch (final NoSuchMethodException nsme3) {
											for (final Class<? extends EnumInterface> enumInterfaceSubClass : new Class[] {
													EnumAutocompleteType.class, EnumBorderType.class,
													EnumContenteditableType.class, EnumCrossoriginCrossOriginType.class,
													EnumDirType.class, EnumDisplayType.class, EnumDraggableType.class,
													EnumEnctypeType.class, EnumFormenctypeEnctypeType.class,
													EnumFormmethodMethodType.class, EnumFormtargetBrowsingContext.class,
													EnumHttpEquivType.class, EnumKindType.class, EnumMediaType.class,
													EnumMethodType.class, EnumOverflowType.class, EnumRelType.class,
													EnumRevType.class, EnumSandboxType.class, EnumScopeType.class,
													EnumShapeType.class, EnumSpellcheckType.class,
													EnumTranslateType.class, EnumTypeButtonType.class,
													EnumTypeContentType.class, EnumTypeInputType.class,
													EnumTypeOlType.class, EnumTypeScriptType.class,
													EnumTypeSimpleContentType.class, EnumWrapType.class }) {
												try {
													nodeClass.getMethod(attrMethodname, enumInterfaceSubClass);
													attrVal = toEnumAttributeValue(
															(Class<? extends EnumInterface<String>>) enumInterfaceSubClass,
															attribute);
													// stops searching, the value has been built with the enum
													break;
												} catch (final NoSuchMethodException nsme4) {
													// go on searching
												} catch (final IllegalArgumentException iae) {
													// stops searching, the value will have to be managed without relying on the dedicated build-in enum
													break;
												}
											}
										}
								    }
								}
							}
							if (attrVal == null) {
								appendable.append(".addAttr(").append("\"").append(attrKey).append("\",")
										.append(convertJavaStringContentToJavaDeclarableString(attribute.getValue()))
										.append(")");
							} else {
								appendable.append(".").append(attrMethodname).append("(").append(attrVal).append(")");
							}
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
			if (!isUnclosable(node)) {
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
