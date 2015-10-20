package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlFormInputSubmit<T> extends HtmlWriterComposite<T, HtmlFormInputSubmit<T>> {

    public HtmlFormInputSubmit(String value) {
        addAttr("submit", "submit");
        addAttr("name", value);
    }

    @Override
    public String getElementName() {
        return ElementType.INPUT.toString();
    }
}
