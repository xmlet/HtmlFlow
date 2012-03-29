package htmlflow;

import java.io.PrintStream;

import htmlflow.elements.HtmlBody;
import htmlflow.elements.HtmlHead;

public class HtmlTemplate<T> extends HtmlWriterComposite<T>{
	public HtmlTemplate(PrintStream out) {
		super(out);
	}
	
	public HtmlHead<T> head(){return addChild(new HtmlHead<T>(out));}
	public HtmlBody<T> body(){return addChild(new HtmlBody<T>(out));}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
	}
	@Override
	public void doWriteAfter(PrintStream out, int depth) {
		out.println("</html>");
	}
}
