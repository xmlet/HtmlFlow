package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlScriptBlock<T> extends HtmlWriterComposite<T>{

	public HtmlScriptBlock<T> code(String msg){addChild(new TextNode<T>(msg));return this;}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<script>");
		tabs(++depth);
	}
	
	@Override
	public void doWriteAfter(PrintStream out,int depth) {
		out.println();
		tabs(depth);
		out.println("</script>");
		tabs(depth);
	}
}
