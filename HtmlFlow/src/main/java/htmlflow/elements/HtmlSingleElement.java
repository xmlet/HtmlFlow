package htmlflow.elements;

import htmlflow.AbstractHtmlWriterElement;
import htmlflow.HtmlWriter;
import htmlflow.attribute.Attribute;

import java.io.PrintStream;

/**
 * HtmlSingleElement represents an HtmlElement without children such as img, br, or hr.
 * Thus the model T is not used byt this kind of element.
 * !!!! TO DO: support model binding on Attributes too.
 *
 * @param <T> The type of the model binding to this HTML element.
 * @param <U> The type of HTML element returned by HtmlSelector methods.
 */
public abstract class HtmlSingleElement<T, U extends HtmlSingleElement>
        extends AbstractHtmlWriterElement<U> implements HtmlWriter<T> {
    /*=========================================================================
	  -------------------------     FIELDS    ---------------------------------
	  =========================================================================*/
    private PrintStream out;

    /*=========================================================================*/
	/*--------------------     HtmlWriter Methods   ----------------------------*/
	/*=========================================================================*/
    /**
     * Sets the current PrintStream.
     * @param out
     */
    public HtmlWriter<T> setPrintStream(PrintStream out){
        this.out = out;
        return this;
    }
    /**
     * @param depth The number of tabs indentation.
     * @param model An optional object model that could be bind to this element.
     */
    @Override
    public final void  write(int depth, T model) {
        tabs(out, depth);
        out.print(getElementValue());
    }
	/*=========================================================================*/
	/*--------------------    Auxiliary Methods    ----------------------------*/
	/*=========================================================================*/
    private String getElementValue() {
        String tag = "<" + getElementName();
        for (Attribute attribute : getAttributes()) {
            tag += attribute.printAttribute();
        }
        return tag + "/>";
    }
}
