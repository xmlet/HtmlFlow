package htmlflow;

import htmlflow.attribute.AttrClass;
import htmlflow.attribute.AttrGeneric;
import htmlflow.attribute.AttrId;
import htmlflow.attribute.Attribute;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base classs for all elements.
 * @param T The type of the model binding to this HTML element.
 * @param U The type of HTML element returned by HtmlSelector methods.
 *
 * @author Miguel Gamboa on 14-01-2016
 */
public abstract class AbstractHtmlWriterElement<T, U extends AbstractHtmlWriterElement> implements HtmlWriter<T>, HtmlSelector<U> {
    /*=========================================================================
	  -------------------------     FIELDS    ---------------------------------
	  =========================================================================*/

    private AttrClass classAttribute;
    private AttrId idAttribute;
    private List<Attribute> attributes;

   	/*=========================================================================
	  -------------------------  CONSTRUCTOR  ---------------------------------
	  =========================================================================*/

    public AbstractHtmlWriterElement() {
        classAttribute = new AttrClass();
        idAttribute = new  AttrId();
        attributes = new LinkedList<Attribute>();
        attributes.add(classAttribute);
        attributes.add(idAttribute);

    }

    /*=========================================================================*/
	/*--------------------     HtmlElement Methods   ----------------------------*/
	/*=========================================================================*/

    /**
     * basic empty name method.
     * Should be overriden in pair with doWriteAfter and doWriteBefore
     * @return
     */
    abstract public String getElementName();

    public void addAttribute(Attribute attr){
        attributes.add(attr);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    /*=========================================================================*/
	/*--------------------     HtmlSelector Methods   ----------------------------*/
	/*=========================================================================*/

    @Override
    public String getClassAttribute() {
        if(classAttribute.getValue() != null){
            return " class=\""+classAttribute.getValue()+"\"";
        }
        return "";
    }

    @Override
    public String getIdAttribute() {
        if(idAttribute.getValue() != null){
            return " id=\""+idAttribute.getValue()+"\"";
        }
        return "";
    }

    @Override
    public U classAttr(String classAttribute) {
        this.classAttribute.setValue(classAttribute);
        return (U) this;
    }

    @Override
    public  U idAttr(String idAttribute) {
        this.idAttribute.setValue(idAttribute);
        return (U) this;
    }

    @Override
    public U addAttr(String name, String value) {
        attributes.add(new AttrGeneric(name, value));
        return (U) this;
    }
}
