package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public abstract class HtmlSingleElement implements HtmlWriter<Object>{
	private PrintStream out;
	private final String element;

	public HtmlSingleElement(String element) {
		this.element = element;
	}

	@Override
	public final void write(int depth, Object model) {
		out.println();
		tabs(depth);
		out.println("<" + element + "/>");
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
