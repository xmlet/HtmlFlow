package htmlflow.elements;

import htmlflow.HtmlWriter;
import static htmlflow.HtmlWriterComposite.tabs;

public class HtmlLinkCss implements HtmlWriter<Object>{
	final String href;
	public HtmlLinkCss(String href) {
	  super();
	  this.href = href;
  }
	@Override
  public String write(int depth, Object model) {
		return "<link rel=\"Stylesheet\" type=\"text/css\" href=\"" + href + "\"/>" + tabs(depth);
  }
}
