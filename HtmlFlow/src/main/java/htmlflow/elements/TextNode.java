package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

public class TextNode<T> implements HtmlWriter<T>{
	
	final PrintStream out;
	private final String msg;
	private final ModelBinder<T> binder;
	
	public TextNode(PrintStream out, String msg) {
		this.out = out;
		this.msg = msg;
		this.binder = null;
	}
	public TextNode(PrintStream out, ModelBinder<T> binder) {
		this.out = out;
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
}
