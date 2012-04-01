package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlDiv<T> extends HtmlWriterComposite<T>{
	
	public HtmlTable<T> table(){return addChild(new HtmlTable<T>());}
	public HtmlDiv<T> text(String msg){addChild(new TextNode<T>(msg));return this;}
	public HtmlDiv<T> text(ModelBinder<T> binder){addChild(new TextNode<T>(binder));return this;}
	public HtmlDiv<T> br(){addChild(new HtmlBr());return this;}
	public HtmlDiv<T> hr(){addChild(new HtmlHr());return this;}
	public HtmlDiv<T> div(){return addChild(new HtmlDiv<T>());}
	public HtmlForm<T> form(String action){return addChild(new HtmlForm<T>(action));}

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
