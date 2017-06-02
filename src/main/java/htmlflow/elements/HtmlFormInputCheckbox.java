package htmlflow.elements;

import htmlflow.HtmlWriter;
import htmlflow.elements.ElementType;

import java.io.PrintStream;

/**
 * Creates an Html checkbox
 */
public class HtmlFormInputCheckbox implements HtmlWriter<Object> {
    PrintStream out;
    String name;

    @Override
    public void write(final int depth, final Object model) {
        out.println("<input type=\"checkbox\" name=\"" + name + "\">");
        out.println("</input>");
    }

    /*
     * (non-Javadoc)
     *
     * @see htmlflow.HtmlElement#getElementName()
     */
    public String getElementName() {
        return ElementType.INPUT.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see htmlflow.HtmlWriter#setPrintStream(java.io.PrintStream)
     */
    @Override
    public HtmlWriter<Object> setPrintStream(final PrintStream out) {
        this.out = out;
        return this;
    }
}