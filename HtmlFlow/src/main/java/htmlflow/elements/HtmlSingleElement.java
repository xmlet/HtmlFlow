package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public abstract class HtmlSingleElement<T> extends HtmlWriterComposite<T, HtmlSingleElement> {
	private final String element;

	public HtmlSingleElement(String element) {
		this.element = element;
	}

    @Override
    public String getElementName(){
        return  element;
    }

}
