package htmlflow.elements;

import java.io.PrintStream;


public class HtmlTitle<T> extends HtmlTextElement<T>{
	public HtmlTitle(PrintStream out) {
		super(out, "title");
	}
}
