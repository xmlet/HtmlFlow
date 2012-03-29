package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

public class HtmlTrFromIterable<S, T extends Iterable<S>> implements HtmlWriter<T>{

	private final HtmlTr<S> tr; 
	
	public HtmlTrFromIterable(PrintStream out, ModelBinder<S>[] binders) {
		tr = new HtmlTr<S>(out);
		for (ModelBinder<S> b : binders) {
			tr.td().text(b);
		}
	}

	@Override
	public void write(int depth, T model) { 
		for (S item : model) {
			tr.write(depth, item);
		}
	}
}
