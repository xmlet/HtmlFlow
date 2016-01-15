package htmlflow;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import htmlflow.attribute.Attribute;
import htmlflow.elements.TextNode;

public abstract class HtmlWriterComposite<T, U extends HtmlWriterComposite> extends AbstractHtmlWriterElement<T,U> {

	/*=========================================================================*/
	/*------------------------- STATIC FIELDS ---------------------------------*/
	/*=========================================================================*/ 
	public static final String NEWLINE = System.getProperty("line.separator");

	/*=========================================================================*/
	/*-------------------------     FIELDS    ---------------------------------*/
	/*=========================================================================*/ 

	/**
	 * @uml.associationEnd  aggregation="shared" inverse="htmlflow.HtmlWriter" multiplicity="(0 -1)" 
	 */
	private final List<HtmlWriter<?>> children;
	protected PrintStream out;

	/*=========================================================================*/
	/*-------------------------  CONSTRUCTOR  ---------------------------------*/
	/*=========================================================================*/ 
	public HtmlWriterComposite() {
		children = new LinkedList<HtmlWriter<?>>();
	}
	
	/*=========================================================================*/
	/*--------------------- HtmlPrinter interface -----------------------------*/
	/*=========================================================================*/
	public HtmlWriter<T> setPrintStream(PrintStream out){
		this.out = out;
		for (HtmlWriter elem : children) {
			elem.setPrintStream(out);
		}
		return this;
	}
	
	@Override
	public final void  write(int depth, T model) { 
		doWriteBefore(out, depth);
		boolean doTab = true;
		if(children!= null && !children.isEmpty() && children.get(0) != null && children.get(0) instanceof TextNode){
		  out.print("");
		  doTab = false;
		}else{
		  out.println();
		  doTab = true;
		}
		for (HtmlWriter elem : children) {
			elem.write(depth+1, model);
		}
		doWriteAfter(out, depth, doTab);
		out.flush();
	}
	
	/*=========================================================================*/
	/*----------------------- Instance Methods --------------------------------*/
	/*=========================================================================*/ 
	
	public<S extends HtmlWriter<?>> S addChild(S child){
		children.add(child);
		return child;
	}
	   
     public void doWriteBefore(PrintStream out, int depth) {
       tabs(depth);
       out.print(getElementValue());
     }

        
    public void doWriteAfter(PrintStream out, int depth, boolean doTab) {
        // RMK : do not insert tabs after a text node
        if(doTab){
          tabs(depth);
        }
        out.println("</"+ getElementName()+">");
    }
    
    protected String getElementValue() {
        String tag = "<" + getElementName();
        for (Attribute attribute : getAttributes()) {
            tag += attribute.printAttribute();
        }
//      return  "<"+ getElementName()+getClassAttribute()+getIdAttribute()+">";
      return  tag+">";
    }

	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}
}