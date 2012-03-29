package htmlflow.elements;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

public class HtmlTrFromIterable<S, T extends Iterable<S>> implements HtmlWriter<T>{

	private final HtmlTr<S> tr; 
	
	public HtmlTrFromIterable(ModelBinder<S>[] binders) {
		tr = new HtmlTr<S>();
		for (ModelBinder<S> b : binders) {
			tr.td().text(b);
		}
	}

	@Override
	public String write(int depth, T model) {
		String res = ""; 
		for (S item : model) {
			res += tr.write(depth, item);
		}
		return res;
	}
}
