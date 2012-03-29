package htmlflow;

import htmlflow.elements.HtmlBody;
import htmlflow.elements.HtmlHead;

public class HtmlTemplate<T> extends HtmlWriterComposite<T>{
	public HtmlHead<T> head(){return addChild(new HtmlHead<T>());}
	public HtmlBody<T> body(){return addChild(new HtmlBody<T>());}

	@Override
	public String doWriteBefore(int depth) {
		return println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">")
		 + println("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
	}
	@Override
	public String doWriteAfter(int depth) {
		return println("</html>");
	}
}
