package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlA<T> extends HtmlWriterComposite<T>{
	private final String href;
	
	public HtmlA<T> text(String msg){addChild(new TextNode<T>(out, msg)); return this;}

	public HtmlA(PrintStream out, String href) {
		super(out);
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
