package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlFormInputSubmit implements HtmlWriter<Object>{

	final PrintStream out;
	final String value;

	public HtmlFormInputSubmit(PrintStream out, String value) {
		this.out = out;
		this.value = value;
	}
	@Override
	public void write(int depth, Object model) {
		out.print("<input type=\"submit\" value=\""+ value + "\"/>");
	}
}
