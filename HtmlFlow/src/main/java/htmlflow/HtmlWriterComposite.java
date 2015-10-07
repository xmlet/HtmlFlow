package htmlflow;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

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
		doWriteBefore(out, depth++);
		for (HtmlWriter elem : children) {
			elem.write(depth, model);
		}
		doWriteAfter(out, --depth);
		out.flush();
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

   	/*=========================================================================*/
	/*-------------------- Selectors Methods ----------------------------*/
	/*=========================================================================*/

    private String classAttribute = null;

    private String idAttribute = null;

    @Override
    public String getClassAttribute() {
        if(classAttribute != null){
            return " class=\""+classAttribute+"\" ";
        }
        return "";
    }

    @Override
    public String getIdAttribute() {
        if(idAttribute != null){
            return " id=\""+idAttribute+"\" ";
        }
        return "";
    }

    @Override
    public HtmlWriterComposite<T> setClassAttribute(String classAttribute) {
        this.classAttribute = classAttribute;
        return this;
    }

    @Override
    public  HtmlWriterComposite<T>  setIdAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
        return this;
    }
}