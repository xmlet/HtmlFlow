package htmlflow.elements;

public class HtmlTh<T> extends HtmlTextElement<T>{
	
	public HtmlA<T> a(String href){return addChild(new HtmlA<T>(href));}
	
    @Override
    public String getElementName() {
      return ElementType.TH.toString();
    }
}
