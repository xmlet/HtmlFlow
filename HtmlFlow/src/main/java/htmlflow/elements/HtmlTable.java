package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlTable<T> extends HtmlWriterComposite<T>{

	public HtmlTr<T> tr(){return addChild(new HtmlTr<T>());}

	public <S, I extends Iterable<S>> HtmlTable<T> trFromIterable(ModelBinder<S>...binders){
		addChild(new HtmlTrFromIterable<S, I>(binders));
		return this;
	}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<table"+getClassAttribute()+getClassAttribute()+">");
		tabs(++depth);
	}

	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println();
		tabs(depth);
		out.print("</table>");
		tabs(depth);
	}
}
