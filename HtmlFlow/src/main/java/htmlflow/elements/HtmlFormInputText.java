package htmlflow.elements;

import htmlflow.HtmlWriter;

public class HtmlFormInputText implements HtmlWriter<Object>{
	
	final String name;
	final String id;
	
	public HtmlFormInputText(String name) {
	  this.name = name;
	  this.id = null;
  }
	public HtmlFormInputText(String name, String id) {
	  this.name = name;
	  this.id = id;
  }
	@Override
	public String write(int depth, Object model) {
		String res = "<input type=\"text\" name=\""+ name + "\"";
		if(id != null) res += " id = \"" + id + "\"";
		return res + "/>";
	}
}
