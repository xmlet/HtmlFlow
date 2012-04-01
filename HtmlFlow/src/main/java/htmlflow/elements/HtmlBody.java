package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlBody<T> extends HtmlWriterComposite<T>{	
	
	public HtmlBody<T> heading(int level, String msg){addChild(new HtmlHeading<T>(level)).text(msg); return this;}
	public HtmlBody<T> heading(int level, ModelBinder<T> binder){addChild(new HtmlHeading<T>(level)).text(binder); return this;}
	public HtmlBody<T> text(String msg){addChild(new TextNode<T>(msg));return this;}
	public HtmlBody<T> text(ModelBinder<T> binder){addChild(new TextNode<T>(binder));return this;}
	public HtmlBody<T> br(){addChild(new HtmlBr());return this;}
	public HtmlBody<T> hr(){addChild(new HtmlHr());return this;}
	public HtmlDiv<T> div(){return addChild(new HtmlDiv<T>());}
	public HtmlForm<T> form(String action){return addChild(new HtmlForm<T>(action));}
	public HtmlTable<T> table(){return addChild(new HtmlTable<T>());}
	public HtmlBody<T> p(String msg){addChild(new HtmlP<T>()).text(msg); return this;}
	public HtmlBody<T> p(ModelBinder<T> binder){addChild(new HtmlP<T>()).text(binder); return this;}
	
	@Override
	public void doWriteBefore(PrintStream out,  int depth) {
		tabs(depth);
		out.println("<body>");
		tabs(++depth);
	}
	
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println();
		tabs(depth);
		out.println("</body>");
	}
}
