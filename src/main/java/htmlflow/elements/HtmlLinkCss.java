package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlLinkCss<T> extends HtmlWriterComposite<T, HtmlLinkCss<T>> {

	public HtmlLinkCss(String href) {
        this.addAttr("rel", "Stylesheet");
        this.addAttr("type", "text/css");
        this.addAttr("href", href);
	}

  @Override
  public String getElementName() {
      return ElementType.LINK.toString();
  }

}
