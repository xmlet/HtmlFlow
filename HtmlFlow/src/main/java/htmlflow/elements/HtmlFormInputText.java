package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlFormInputText extends HtmlWriterComposite{

	public HtmlFormInputText(String name) {
        addAttr("name", name);
	}

	public HtmlFormInputText(String name, String id) {
        addAttr("name", name);
        addAttr("id", id);
	}

    @Override
    public String getElementName() {
        return ElementType.INPUT.toString();
    }
}
