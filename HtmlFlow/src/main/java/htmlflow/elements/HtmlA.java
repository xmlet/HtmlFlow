package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlA<T> extends HtmlWriterComposite{
	private final String href;
	
	public HtmlA<T> text(String msg){addChild(new TextNode<T>(msg)); return this;}

	public HtmlA(String href) {
		this.href = href;
	}
	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.print("<a href=\"" + href+ "\">");
	}
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.print("</a>");
	}
}
