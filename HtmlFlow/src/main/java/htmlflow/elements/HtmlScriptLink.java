package htmlflow.elements;

import htmlflow.HtmlWriter;

import static htmlflow.HtmlWriterComposite.println;
import static htmlflow.HtmlWriterComposite.tabs;

public class HtmlScriptLink implements HtmlWriter<Object>{
	final String src;
	public HtmlScriptLink(String src) {
	  this.src = src;
  }
	@Override
  public String write(int depth, Object model) {
		return println("<script type=\"text/javascript\" src=\"" + src + "\"></script>")+ tabs(depth);
  }
}
