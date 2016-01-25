package htmlflow;

import htmlflow.attribute.Attribute;

import java.util.List;

/**
 * @author Miguel Gamboa on 15-01-2016.
 */
public interface HtmlElement<T> {
    /**
     * @return String with the name of this element.
     */
    public String getElementName();

    /**
     * @return The Attribute objects contained by this element.
     */
    Iterable<Attribute> getAttributes();
    /**
     * create a generic attribute
     * @param name
     * @param value
     * @return
     */
    T addAttr(String name, String value);
}
