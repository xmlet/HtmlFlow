package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlFormSelect implements HtmlWriter<Object>{
	PrintStream out;
	final String name;
	final String[] options;
	
	public HtmlFormSelect(String name, String...options) {
		this.name = name;
		this.options = options;
	}
	@Override
	public void write(int depth, Object model) {
		out.println("<select name=\""+ name+ "\">");
		tabs(++depth);
		for (String op : options) {
			out.println("<option>" + op + "</option>");
			tabs(depth);
		}
		tabs(--depth);
		out.println("</select>");
		tabs(depth);
	}
	
	@Override
	public HtmlWriter<Object> setPrintStream(PrintStream out) {
		this.out = out;
		return this;
	}

	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}
}
