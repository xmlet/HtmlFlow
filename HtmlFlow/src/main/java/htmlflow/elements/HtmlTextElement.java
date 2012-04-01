package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public abstract class HtmlTextElement<T> extends HtmlWriterComposite<T>{

	public final void text(String msg){addChild(new TextNode<T>(msg)); }
	public final void text(ModelBinder<T> binder){addChild(new TextNode<T>(binder));}

	private final String element;
	
	public HtmlTextElement(String element) {
		this.element = element;
	}
	
	@Override
	public final void doWriteBefore(PrintStream out, int depth) {
		out.print("<" + element + ">");
	}
	@Override
	public final void doWriteAfter(PrintStream out, int depth) {
		out.print("</" + element + ">");
	}
}
