package htmlflow.elements;

public class HtmlHeading<T> extends HtmlTextElement<T> {

  public HtmlHeading(int level) {
    super("h" + level);
  }

  @Override
  public String getElementName() {
    return element;
  }
}