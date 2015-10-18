package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlFormInputSubmit implements HtmlWriter<Object>{

	PrintStream out;
	final String value;

	public HtmlFormInputSubmit(String value) {
		this.value = value;
	}
	
	@Override
	public void write(int depth, Object model) {
		out.print("<input type=\"submit\" value=\""+ value + "\"/>");
	}
	
	@Override
	public HtmlWriter<Object> setPrintStream(PrintStream out) {
		this.out = out;
		return this;
	}

}
