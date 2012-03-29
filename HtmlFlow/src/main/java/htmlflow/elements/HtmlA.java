package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlA<T> extends HtmlWriterComposite<T>{
	final String href;
	public HtmlA<T> text(String msg){addChild(new TextNode<T>(msg)); return this;}

	public HtmlA(String href) {
		this.href = href;
	}
	@Override
	public String doWriteBefore(int depth) {
		return "<a href=\"" + href+ "\">";
	}
	@Override
	public String doWriteAfter(int depth) {
		return"</a>";
	}
}
