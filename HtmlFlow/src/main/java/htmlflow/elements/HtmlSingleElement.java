package htmlflow.elements;

import htmlflow.HtmlWriter;

import static htmlflow.HtmlWriterComposite.NEWLINE;
import static htmlflow.HtmlWriterComposite.println;
import static htmlflow.HtmlWriterComposite.tabs;

public abstract class HtmlSingleElement implements HtmlWriter<Object>{
	
	private final String element;
	
	public HtmlSingleElement(String element) {
		this.element = element;
	}

	@Override
	public final String write(int depth, Object model) {
		return NEWLINE + tabs(depth) + println("<" + element + "/>") + tabs(depth);
	}

}
