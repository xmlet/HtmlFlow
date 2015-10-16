package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlA<T> extends HtmlWriterComposite<T, HtmlA> {
  private final String href;

  public HtmlA<T> text(String msg) {
    addChild(new TextNode<T>(msg));
    return this;
  }

  public HtmlA(String href) {
    this.href = href;
  }

  protected String getElementValue(PrintStream out) {
    return "<" + getElementName() + " href=\"" + href + "\"" + getClassAttribute() + getIdAttribute() + ">";
  }

  @Override
  public String getElementName() {
    return ElementType.A.toString();
  }
}
