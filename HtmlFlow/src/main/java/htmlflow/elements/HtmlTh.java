package htmlflow.elements;

import java.io.PrintStream;

public class HtmlTh<T> extends HtmlTextElement<T>{
	
	public HtmlA<T> a(String href){return addChild(new HtmlA<T>(out, href));}

	public HtmlTh(PrintStream out) {
		super(out, "th");
	}
}
