package htmlflow.elements;

import java.io.PrintStream;

public class HtmlHeading<T> extends HtmlTextElement<T>{
	public HtmlHeading(PrintStream out, int level) {
		super(out, "h" + level);
	}
}