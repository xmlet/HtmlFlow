package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;

public class HtmlLinkCss implements HtmlWriter<Object>{
	final PrintStream out;
	final String href;
	
	public HtmlLinkCss(PrintStream out, String href) {
		this.out = out;
		this.href = href;
	}

	@Override
	public void write(int depth, Object model) {
		out.print("<link rel=\"Stylesheet\" type=\"text/css\" href=\"" + href + "\"/>");
		tabs(depth);
	}
	
	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}
}
