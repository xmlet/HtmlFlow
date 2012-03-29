package htmlflow.elements;

import htmlflow.HtmlWriter;

public class HtmlFormInputSubmit implements HtmlWriter<Object>{
	final String value;
	public HtmlFormInputSubmit(String value) {
	  this.value = value;
  }
	@Override
	public String write(int depth, Object model) {
		return "<input type=\"submit\" value=\""+ value + "\"/>";
	}
}
