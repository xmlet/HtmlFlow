package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

public class HtmlScriptBlock<T> extends HtmlWriterComposite<T>{

	public HtmlScriptBlock<T> code(String msg){addChild(new TextNode<T>(msg));return this;}

	@Override
	public String doWriteBefore(int depth) {
		return println("<script>") + tabs(++depth);
	}
	@Override
	public String doWriteAfter(int depth) {
		return NEWLINE + tabs(depth) + println("</script>") + tabs(depth);
	}
}
