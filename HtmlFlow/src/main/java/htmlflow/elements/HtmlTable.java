package htmlflow.elements;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlTable<T> extends HtmlWriterComposite<T>{
	public HtmlTr<T> tr(){return addChild(new HtmlTr<T>());}
	public <S, I extends Iterable<S>> HtmlTable<T> trFromIterable(ModelBinder<S>...binders){
		addChild(new HtmlTrFromIterable<S, I>(binders));
		return this;
	}
	@Override
	public String doWriteBefore(int depth) {
		return println("<table>") + tabs(++depth);
	}
	@Override
	public String doWriteAfter(int depth) {
		return println("")+ tabs(depth) + "</table>" + tabs(depth);
	}
}
