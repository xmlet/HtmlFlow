package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlScriptLink implements HtmlWriter<Object>{
	
	final PrintStream out;
	final String src;

	public HtmlScriptLink(PrintStream out, String src) {
		this.out = out;
		this.src = src;
	}
	
	@Override
	public void write(int depth, Object model) {
		out.println("<script type=\"text/javascript\" src=\"" + src + "\"></script>");
		tabs(depth);
	}
	
	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}
}
