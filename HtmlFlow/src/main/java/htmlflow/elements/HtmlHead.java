package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlHead<T> extends HtmlWriterComposite<T>{
	
	public HtmlHead<T> title(String msg){addChild(new HtmlTitle<T>()).text(msg);return this;}
	public HtmlHead<T> scriptLink(String src){addChild(new HtmlScriptLink(src));return this;}
	public HtmlScriptBlock<T> scriptBlock(){return addChild(new HtmlScriptBlock<T>());}
	public HtmlHead<T> linkCss(String href){addChild(new HtmlLinkCss(href));return this;}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		tabs(depth);
		out.println("<head>");
		tabs(depth+1);
	}
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println();
		tabs(depth);
		out.println("</head>");
	}
}
