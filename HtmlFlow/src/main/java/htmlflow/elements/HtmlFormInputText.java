package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlFormInputText implements HtmlWriter<Object>{

	PrintStream out;
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
	public void write(int depth, Object model) {
		out.print("<input type=\"text\" name=\""+ name + "\"");
		if(id != null) out.print(" id = \"" + id + "\"");
		out.print("/>");
	}
	
	@Override
	public HtmlWriter<Object> setPrintStream(PrintStream out) {
		this.out = out;
		return this;
	}

}
