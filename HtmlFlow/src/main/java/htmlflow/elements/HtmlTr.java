package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;

public class HtmlTr<T> extends HtmlWriterComposite<T>{
	
	public HtmlTd<T> td(){return addChild(new HtmlTd<T>());}
	public HtmlTh<T> th(){return addChild(new HtmlTh<T>());}
	
    @Override
    public String getElementName() {
      return ElementType.TR.toString();
    }
}
