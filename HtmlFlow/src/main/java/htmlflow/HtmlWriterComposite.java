package htmlflow;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import htmlflow.attribute.AttrClass;
import htmlflow.attribute.AttrGeneric;
import htmlflow.attribute.AttrId;
import htmlflow.attribute.Attribute;
import htmlflow.elements.TextNode;

public abstract class HtmlWriterComposite<T, U extends HtmlWriterComposite> implements HtmlWriter<T>, HtmlSelector<U> {

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
    private List<Attribute> attributes;
	/*=========================================================================*/
	/*-------------------------  CONSTRUCTOR  ---------------------------------*/
	/*=========================================================================*/ 
	public HtmlWriterComposite() {
		children = new LinkedList<HtmlWriter<?>>();
        attributes = new LinkedList<Attribute>();
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
        for (Attribute attribute : attributes) {
            tag += attribute.getAttribute();
        }
//      return  "<"+ getElementName()+getClassAttribute()+getIdAttribute()+">";
      return  tag+">";
    }
    
    /**
     * basic empty name method.
     * Should be overriden in pair with doWriteAfter and doWriteBefore
     * @return
     */
    public String getElementName(){
        return "";
    };

    public void addAttribute(Attribute attr){
        attributes.add(attr);
    }

	/*=========================================================================*/
	/*-------------------- auxiliar Methods ----------------------------*/
	/*=========================================================================*/ 
	
	public final void tabs(int depth){
		for (int i = 0; i < depth; i++) out.print("\t");
	}

   	/*=========================================================================*/
	/*-------------------- Selectors Methods ----------------------------*/
	/*=========================================================================*/

    private AttrClass classAttribute = new AttrClass();

    private AttrId idAttribute2 = new  AttrId(); 

    @Override
    public String getClassAttribute() {
        if(classAttribute.getValue() != null){
            return " class=\""+classAttribute.getValue()+"\"";
        }
        return "";
    }

    @Override
    public String getIdAttribute() {
        if(idAttribute2.getValue() != null){
            return " id=\""+idAttribute2.getValue()+"\"";
        }
        return "";
    }

    @Override
    public U classAttr(String classAttribute) {
        this.classAttribute.setValue(classAttribute);
        return (U) this;
    }

    @Override
    public  U idAttr(String idAttribute) {
        idAttribute2.setValue(idAttribute);
        return (U) this;
    }

    @Override
    public U addAttr(String name, String value) {
        attributes.add(new AttrGeneric(name, value));
        return (U) this;
    }

}