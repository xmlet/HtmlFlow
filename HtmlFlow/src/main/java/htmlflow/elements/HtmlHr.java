package htmlflow.elements;

import htmlflow.HtmlSingleElement;

public class HtmlHr<T, U extends HtmlHr> extends HtmlSingleElement<T, U> {
	@Override
	public final String getElementName() {
		return ElementType.HR.toString();
	}
}
