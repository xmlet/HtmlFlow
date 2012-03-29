package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlFormInputText implements HtmlWriter<Object>{

	final PrintStream out;
	final String name;
	final String id;

	public HtmlFormInputText(PrintStream out, String name) {
		this.out = out;
		this.name = name;
		this.id = null;
	}
	public HtmlFormInputText(PrintStream out, String name, String id) {
		this.out = out;
		this.name = name;
		this.id = id;
	}
	@Override
	public void write(int depth, Object model) {
		out.print("<input type=\"text\" name=\""+ name + "\"");
		if(id != null) out.print(" id = \"" + id + "\"");
		out.print("/>");
	}
}
