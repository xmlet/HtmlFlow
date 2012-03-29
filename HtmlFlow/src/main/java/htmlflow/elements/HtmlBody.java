package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlBody<T> extends HtmlWriterComposite<T>{	

	public HtmlBody(PrintStream out) {
		super(out);
	}
	
	public HtmlBody<T> heading(int level, String msg){addChild(new HtmlHeading<T>(out, level)).text(msg); return this;}
	public HtmlBody<T> heading(int level, ModelBinder<T> binder){addChild(new HtmlHeading<T>(out, level)).text(binder); return this;}
	public HtmlBody<T> text(String msg){addChild(new TextNode<T>(out, msg));return this;}
	public HtmlBody<T> text(ModelBinder<T> binder){addChild(new TextNode<T>(out, binder));return this;}
	public HtmlBody<T> br(){addChild(new HtmlBr(out));return this;}
	public HtmlBody<T> hr(){addChild(new HtmlHr(out));return this;}
	public HtmlDiv<T> div(){return addChild(new HtmlDiv<T>(out));}
	public HtmlForm<T> form(String action){return addChild(new HtmlForm<T>(out, action));}
	public HtmlTable<T> table(){return addChild(new HtmlTable<T>(out));}
	public HtmlBody<T> p(String msg){addChild(new HtmlP<T>(out)).text(msg); return this;}
	public HtmlBody<T> p(ModelBinder<T> binder){addChild(new HtmlP<T>(out)).text(binder); return this;}
	
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
