package htmlflow;

import java.util.LinkedList;
import java.util.List;

public abstract class HtmlWriterComposite<T> implements HtmlWriter<T>{

	/*=========================================================================*/
	/*------------------------- STATIC FIELDS ---------------------------------*/
	/*=========================================================================*/ 
	public static final String NEWLINE = System.getProperty("line.separator");

	/*=========================================================================*/
	/*-------------------------     FIELDS    ---------------------------------*/
	/*=========================================================================*/ 

	List<HtmlWriter<?>> children = new LinkedList<HtmlWriter<?>>();

	/*=========================================================================*/
	/*--------------------- HtmlPrinter interface -----------------------------*/
	/*=========================================================================*/ 
	@Override
	public final String write(int depth, T model) { 
		String res = doWriteBefore(depth++);
		for (HtmlWriter elem : children) {
			res += elem.write(depth, model);
		}
		res += doWriteAfter(--depth);
		return res;
	}
	/*=========================================================================*/
	/*----------------------- Instance Methods --------------------------------*/
	/*=========================================================================*/ 
	
	public<S extends HtmlWriter<?>> S addChild(S child){
		children.add(child);
		return child;
	}

	public abstract String doWriteBefore(int depth);
	public abstract String doWriteAfter(int depth);
	
	/*=========================================================================*/
	/*-------------------- STATIC auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final static String tabs(int depth){
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < depth; i++) res.append("\t");
		return res.toString();
	}
	public final static String println(String msg){
		return msg + NEWLINE;
	}
}