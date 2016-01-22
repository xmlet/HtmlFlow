package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

/**
 * @author  mcarvalho
 */
public class HtmlTrFromIterable<S, T extends Iterable<S>> implements HtmlWriter<T>{

	/**
	 * @uml.property  name="tr"
	 * @uml.associationEnd  
	 */
	private final HtmlTr<S> tr; 
	
	public HtmlTrFromIterable(ModelBinder<S, ?>[] binders) {
		tr = new HtmlTr<S>();
		for (ModelBinder<S, ?> b : binders) {
			tr.td().text(b);
		}
	}

	@Override
	public void write(int depth, T model) { 
		for (S item : model) {
			tr.write(depth, item);
		}
	}

	@Override
	public HtmlWriter<T> setPrintStream(PrintStream out) {
		tr.setPrintStream(out);
		return this;
	}
}
