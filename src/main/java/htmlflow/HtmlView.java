package htmlflow;

import java.io.PrintStream;

import htmlflow.elements.ElementType;
import htmlflow.elements.HtmlBody;
import htmlflow.elements.HtmlHead;

public class HtmlView<T> extends HtmlWriterComposite<T, HtmlView<T>>{
	
	public HtmlHead<T> head(){return addChild(new HtmlHead<T>());}
	public HtmlBody<T> body(){return addChild(new HtmlBody<T>());}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.print("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
	}
	
    @Override
    public String getElementName() {
      return ElementType.HTML.toString();
    }
}
