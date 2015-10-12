package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlScriptBlock<T> extends HtmlWriterComposite<T, HtmlScriptBlock>{

	public HtmlScriptBlock<T> code(String msg){addChild(new TextNode<T>(msg));return this;}

    @Override
    public String getElementName() {
      return ElementType.SCRIPT.toString();
    }
}
