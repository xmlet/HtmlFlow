package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlTr<T> extends HtmlWriterComposite<T>{

	public HtmlTd<T> td(){return addChild(new HtmlTd<T>());}
	public HtmlTh<T> th(){return addChild(new HtmlTh<T>());}
	@Override
	public String doWriteBefore(int depth) {
		return println("<tr>") + tabs(++depth);
	}
	@Override
	public String doWriteAfter(int depth) {
		return println("") + tabs(depth) + println("</tr>") + tabs(depth);
	}
}
