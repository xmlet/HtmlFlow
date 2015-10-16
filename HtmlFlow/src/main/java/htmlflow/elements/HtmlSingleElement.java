package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.HtmlWriterComposite;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
