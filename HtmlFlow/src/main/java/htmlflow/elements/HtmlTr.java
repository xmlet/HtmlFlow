package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlTr<T> extends HtmlWriterComposite<T>{

	public HtmlTr(PrintStream out) {
		super(out);
	}
	
	public HtmlTd<T> td(){return addChild(new HtmlTd<T>(out));}
	public HtmlTh<T> th(){return addChild(new HtmlTh<T>(out));}
	
	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<tr>");
		tabs(++depth);
	}
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println("");
		tabs(depth);
		out.println("</tr>");
		tabs(depth);
	}
}
