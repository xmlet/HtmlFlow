package htmlflow;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import htmlflow.attribute.Attribute;

/**
 * @param <T> The type of the model binding to this HTML element.
 * @param <U> The type of HTML element returned by HtmlSelector methods.
 */
public abstract class HtmlWriterComposite<T, U extends HtmlWriterComposite>
		extends AbstractHtmlElementSelector<U> implements HtmlWriter<T> {

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
	private PrintStream out;

	/*=========================================================================*/
	/*-------------------------  CONSTRUCTOR  ---------------------------------*/
	/*=========================================================================*/ 
	public HtmlWriterComposite() {
		children = new LinkedList<HtmlWriter<?>>();
	}
	
	/*=========================================================================*/
	/*--------------------- HtmlWriter interface -----------------------------*/
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
       tabs(out, depth);
       out.print(getElementValue());
     }

        
    public void doWriteAfter(PrintStream out, int depth, boolean doTab) {
        // RMK : do not insert tabs after a text node
        if(doTab){
          tabs(out, depth);
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
}