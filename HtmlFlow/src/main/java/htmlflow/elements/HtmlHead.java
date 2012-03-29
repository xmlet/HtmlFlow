package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlHead<T> extends HtmlWriterComposite<T>{	
	public HtmlHead<T> title(String msg){addChild(new HtmlTitle<T>()).text(msg);return this;}
	public HtmlHead<T> scriptLink(String src){addChild(new HtmlScriptLink(src));return this;}
	public HtmlScriptBlock<T> scriptBlock(){return addChild(new HtmlScriptBlock<T>());}
	public HtmlHead<T> linkCss(String href){addChild(new HtmlLinkCss(href));return this;}
	
	@Override
  public String doWriteBefore(int depth) {
	  return tabs(depth) + println("<head>") + tabs(depth+1);
  }
	@Override
  public String doWriteAfter(int depth) {
		return NEWLINE + tabs(depth) + println("</head>");
  }
}
