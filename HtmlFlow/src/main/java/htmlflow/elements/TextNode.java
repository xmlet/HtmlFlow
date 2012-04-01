package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

public class TextNode<T> implements HtmlWriter<T>{
	
	PrintStream out;
	private final String msg;
	private final ModelBinder<T> binder;
	
	public TextNode(String msg) {
		this.msg = msg;
		this.binder = null;
	}
	public TextNode(ModelBinder<T> binder) {
		this.msg = null;
		this.binder = binder;
	}
	@Override
	public void write(int depth, T model) {
		if(binder == null){
			out.print(msg);
		}
		else{
			assert(binder != null);
			binder.bind(out, model);
		}
	}
	@Override
	public HtmlWriter<T> setPrintStream(PrintStream out) {
		this.out = out;
		return this;
	}
}
