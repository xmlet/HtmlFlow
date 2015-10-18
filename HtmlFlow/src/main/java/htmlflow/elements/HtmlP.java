package htmlflow.elements;

public class HtmlP<T> extends HtmlTextElement<T, HtmlP<T>>{
	public HtmlP() {
		super("p");
	}

    @Override
    public String getElementName() {
      return ElementType.P.toString();
    }
}
