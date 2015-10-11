package htmlflow;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import htmlflow.elements.TextNode;

public abstract class HtmlWriterComposite<T> implements HtmlWriter<T>, HtmlSelector<HtmlWriterComposite<T>> {

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
		if(children.get(0) != null && children.get(0) instanceof TextNode){
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
       out.print(getElementValue(out));
     }

    protected String getElementValue(PrintStream out) {
     return  "<"+ getElementName()+getClassAttribute()+getIdAttribute()+">";
    }
     
        
    public void doWriteAfter(PrintStream out, int depth, boolean doTab) {
        // RMK : do not insert tabs after a text node
        if(doTab){
          tabs(depth);
        }
        out.println("</"+ getElementName()+">");
    }
    
    /**
     * basic empty name method.
     * Should be overriden in pair with doWriteAfter and doWriteBefore
     * @return
     */
    public String getElementName(){
        return "";
    };
	
	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}

   	/*=========================================================================*/
	/*-------------------- Selectors Methods ----------------------------*/
	/*=========================================================================*/

    private String classAttribute = null;

    private String idAttribute = null;

    @Override
    public String getClassAttribute() {
        if(classAttribute != null){
            return " class=\""+classAttribute+"\"";
        }
        return "";
    }

    @Override
    public String getIdAttribute() {
        if(idAttribute != null){
            return " id=\""+idAttribute+"\"";
        }
        return "";
    }

    @Override
    public HtmlWriterComposite<T> classAttr(String classAttribute) {
        this.classAttribute = classAttribute;
        return this;
    }

    @Override
    public  HtmlWriterComposite<T>  idAttr(String idAttribute) {
        this.idAttribute = idAttribute;
        return this;
    }
}