package htmlflow.elements;

import htmlflow.HtmlWriterComposite;
import htmlflow.attribute.AttrGeneric;
import htmlflow.attribute.AttributeType;

public class HtmlA<T> extends HtmlWriterComposite<T, HtmlA<T>> {

  public HtmlA<T> text(String msg) {
    addChild(new TextNode<T>(msg));
    return this;
  }

  public HtmlA(String href) {
    addAttribute(new AttrGeneric(AttributeType.HREF.toString(), href));
  }

  @Override
  public String getElementName() {
    return ElementType.A.toString();
  }
}
