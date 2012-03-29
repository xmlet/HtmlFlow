package htmlflow;

import java.io.PrintStream;
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

	private final List<HtmlWriter<?>> children;
	protected final PrintStream out; 

	/*=========================================================================*/
	/*-------------------------  CONSTRUCTOR  ---------------------------------*/
	/*=========================================================================*/ 
	public HtmlWriterComposite(PrintStream out) {
		this.out = out;
		children = new LinkedList<HtmlWriter<?>>();
	}

	
	/*=========================================================================*/
	/*--------------------- HtmlPrinter interface -----------------------------*/
	/*=========================================================================*/ 
	@Override
	public final void  write(int depth, T model) { 
		doWriteBefore(out, depth++);
		for (HtmlWriter elem : children) {
			elem.write(depth, model);
		}
		doWriteAfter(out, --depth);
	}
	/*=========================================================================*/
	/*----------------------- Instance Methods --------------------------------*/
	/*=========================================================================*/ 
	
	public<S extends HtmlWriter<?>> S addChild(S child){
		children.add(child);
		return child;
	}

	public abstract void doWriteBefore(PrintStream out, int depth);
	public abstract void doWriteAfter(PrintStream out, int depth);
	
	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}
}