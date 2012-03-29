package htmlflow.elements;

import java.io.PrintStream;

public class HtmlTd<T> extends HtmlTextElement<T>{
	public HtmlA<T> a(String href){return addChild(new HtmlA<T>(out, href));}

	public HtmlTd(PrintStream out) {
		super(out, "td");
	}
}
