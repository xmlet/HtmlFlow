package htmlflow.elements;

import java.io.PrintStream;

public class HtmlP<T> extends HtmlTextElement<T>{
	public HtmlP(PrintStream out) {
		super(out, "p");
	}
}
