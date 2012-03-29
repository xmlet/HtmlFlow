package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlHead<T> extends HtmlWriterComposite<T>{
	
	public HtmlHead(PrintStream out) {
		super(out);
	}
	
	public HtmlHead<T> title(String msg){addChild(new HtmlTitle<T>(out)).text(msg);return this;}
	public HtmlHead<T> scriptLink(String src){addChild(new HtmlScriptLink(out, src));return this;}
	public HtmlScriptBlock<T> scriptBlock(){return addChild(new HtmlScriptBlock<T>(out));}
	public HtmlHead<T> linkCss(String href){addChild(new HtmlLinkCss(out, href));return this;}

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
