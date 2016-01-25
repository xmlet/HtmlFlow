package htmlflow;

import htmlflow.attribute.AttrClass;
import htmlflow.attribute.AttrGeneric;
import htmlflow.attribute.AttrId;
import htmlflow.attribute.Attribute;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base classs for all elements.
 * @param <U> The type of HTML element returned by HtmlSelector methods.
 *
 * @author Miguel Gamboa on 14-01-2016
 */
public abstract class AbstractHtmlElementSelector<U extends AbstractHtmlElementSelector>
        implements HtmlElement<U>, HtmlSelector<U> {
    /*=========================================================================
	  -------------------------     FIELDS    ---------------------------------
	  =========================================================================*/

    private AttrClass classAttribute;
    private AttrId idAttribute;
    private List<Attribute> attributes;

   	/*=========================================================================
	  -------------------------  CONSTRUCTOR  ---------------------------------
	  =========================================================================*/

    public AbstractHtmlElementSelector() {
        classAttribute = new AttrClass();
        idAttribute = new  AttrId();
        attributes = new LinkedList<>();
        attributes.add(classAttribute);
        attributes.add(idAttribute);

    }
    /*=========================================================================*/
	/*--------------------    Auxiliary Methods    ----------------------------*/
	/*=========================================================================*/

    public final void tabs(PrintStream out, int depth){
        for (int i = 0; i < depth; i++)
            out.print("\t");
    }

    /*=========================================================================*/
	/*--------------------     HtmlElement Methods   ----------------------------*/
	/*=========================================================================*/

    @Override
    public Iterable<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public U addAttr(String name, String value) {
        attributes.add(new AttrGeneric(name, value));
        return (U) this;
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
}
