package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlHead<T> extends HtmlWriterComposite<T, HtmlHead<T>>{
	
	public HtmlHead<T> title(String msg){addChild(new HtmlTitle<T>()).text(msg);return this;}
	public HtmlHead<T> scriptLink(String src){addChild(new HtmlScriptLink<T>(src));return this;}
	public HtmlScriptBlock<T> scriptBlock(){return addChild(new HtmlScriptBlock<T>());}
	public HtmlHead<T> linkCss(String href){addChild(new HtmlLinkCss<T>(href));return this;}

	@Override
	public String getElementName() {
	  return ElementType.HEAD.toString();
	}
}
