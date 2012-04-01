package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlScriptLink implements HtmlWriter<Object>{
	
	PrintStream out;
	final String src;

	public HtmlScriptLink(String src) {
		this.src = src;
	}
	
	@Override
	public void write(int depth, Object model) {
		out.println("<script type=\"text/javascript\" src=\"" + src + "\"></script>");
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
