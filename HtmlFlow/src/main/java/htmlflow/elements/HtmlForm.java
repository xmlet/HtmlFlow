package htmlflow.elements;
import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlForm<T> extends HtmlWriterComposite<T>{
	public HtmlForm<T> text(String msg){addChild(new TextNode<T>(out, msg));return this;}
	public HtmlForm<T> br(){addChild(new HtmlBr(out));return this;}
	public HtmlForm<T> select(String name, String...options){addChild(new HtmlFormSelect(out, name, options));return this;}
	public HtmlForm<T> inputText(String name){addChild(new HtmlFormInputText(out, name));return this;}	
	public HtmlForm<T> inputText(String name, String id){addChild(new HtmlFormInputText(out, name, id));return this;}
	public HtmlForm<T> inputSubmit(String value){addChild(new HtmlFormInputSubmit(out, value));return this;}

	final private String action;

	public HtmlForm(PrintStream out, String action) {
		super(out);
		this.action = action;
	}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.print(String.format("<form action=\"%s\" method=\"%s\" enctype=\"%s\">",
				action,
				"post",
				"application/x-www-form-urlencoded"));
		tabs(++depth);
	}
	
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println("");
		tabs(depth);
		out.println("</form>");
		tabs(depth);
	}
}
