package htmlflow.elements;

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;

public class HtmlTable<T> extends HtmlWriterComposite<T, HtmlTable<T>>{

	public HtmlTr<T> tr(){return addChild(new HtmlTr<T>());}

	public <S, I extends Iterable<S>> HtmlTable<T> trFromIterable(ModelBinder<S, ?>...binders){
		addChild(new HtmlTrFromIterable<S, I>(binders));
		return this;
	}
	
    @Override
    public String getElementName() {
      return ElementType.TABLE.toString();
    }
}
