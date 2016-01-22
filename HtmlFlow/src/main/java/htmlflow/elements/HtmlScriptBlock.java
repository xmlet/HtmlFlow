package htmlflow.elements;

import htmlflow.HtmlWriterComposite;
import htmlflow.TextNode;

public class HtmlScriptBlock<T> extends HtmlWriterComposite<T, HtmlScriptBlock<T>>{

	public HtmlScriptBlock<T> code(String msg){addChild(new TextNode<T>(msg));return this;}

    @Override
    public String getElementName() {
      return ElementType.SCRIPT.toString();
    }
}
