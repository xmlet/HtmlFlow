package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlMeta<T> extends HtmlWriterComposite<T, HtmlMeta<T>> {
    
    @Override
    public String getElementName() {
        return ElementType.META.toString();
    }
    
}
