package htmlflow.elements;
import java.io.PrintStream;

import htmlflow.HtmlWriterComposite;
import htmlflow.attribute.AttrGeneric;
import htmlflow.attribute.AttributeType;

public class HtmlForm<T> extends HtmlWriterComposite<T, HtmlForm<T>>{
	public HtmlForm<T> text(String msg){addChild(new TextNode<T>(msg));return this;}
	public HtmlForm<T> br(){addChild(new HtmlBr());return this;}
	public HtmlForm<T> select(String name, String...options){addChild(new HtmlFormSelect(name, options));return this;}
	public HtmlForm<T> inputText(String name){addChild(new HtmlFormInputText(name));return this;}	
	public HtmlForm<T> inputText(String name, String id){addChild(new HtmlFormInputText(name, id));return this;}
	public HtmlForm<T> inputSubmit(String value){addChild(new HtmlFormInputSubmit(value));return this;}

	final private String action;

	public HtmlForm(String action) {
		this.action = action;
		addAttribute(new AttrGeneric(AttributeType.ACTION.toString(), action));
		addAttribute(new AttrGeneric(AttributeType.METHOD.toString(), "post"));
		addAttribute(new AttrGeneric(AttributeType.ENCTYPE.toString(), "application/x-www-form-urlencoded"));
	}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.print(String.format("<form action=\"%s\" method=\"%s\" enctype=\"%s\">",
				action,
				"post",
				"application/x-www-form-urlencoded"));
		tabs(++depth);
	}

  @Override
  public String getElementName() {
    return "form";
  }
}
