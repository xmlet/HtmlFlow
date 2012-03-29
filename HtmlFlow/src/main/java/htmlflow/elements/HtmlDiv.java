package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlDiv<T> extends HtmlWriterComposite<T>{
	
	public HtmlDiv(PrintStream out) {
		super(out);
	}
	
	public HtmlTable<T> table(){return addChild(new HtmlTable<T>(out));}
	public HtmlDiv<T> text(String msg){addChild(new TextNode<T>(out, msg));return this;}
	public HtmlDiv<T> text(ModelBinder<T> binder){addChild(new TextNode<T>(out, binder));return this;}
	public HtmlDiv<T> br(){addChild(new HtmlBr(out));return this;}
	public HtmlDiv<T> hr(){addChild(new HtmlHr(out));return this;}
	public HtmlDiv<T> div(){return addChild(new HtmlDiv<T>(out));}
	public HtmlForm<T> form(String action){return addChild(new HtmlForm<T>(out, action));}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<div>");
		tabs(depth+1);
	}
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println();
		tabs(depth);
		out.println("</div>");
	}
}
