package htmlflow.elements;

public class HtmlTd<T> extends HtmlTextElement<T>{
	public HtmlA<T> a(String href){return addChild(new HtmlA<T>(href));}

	public HtmlTd() {
		super("td");
	}
}
