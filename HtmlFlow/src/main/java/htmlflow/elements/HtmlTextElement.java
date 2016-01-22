package htmlflow.elements;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;
import htmlflow.TextNode;

public abstract class HtmlTextElement<T, U extends HtmlTextElement> extends HtmlWriterComposite<T, HtmlTextElement>{

	public final void text(String msg){addChild(new TextNode<T>(msg)); }
	public final void text(ModelBinder<T, ?> binder){addChild(new TextNode<T>(binder));}

	protected final String element;
	
	public HtmlTextElement() {
	  element = "";
	}
	
	public HtmlTextElement(String element) {
		this.element = element;
	}

    @Override
    public String getElementName(){
        return  element;
    }

}
