package htmlflow.elements;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

public class TextNode<T> implements HtmlWriter<T>{

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
	public String write(int depth, T model) {
		if(binder == null){
			return msg;
		}
		else{
			assert(binder != null);
			return binder.bind(model);
		}
	}
}
