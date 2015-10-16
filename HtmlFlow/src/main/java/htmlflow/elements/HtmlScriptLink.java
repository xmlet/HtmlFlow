package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.HtmlWriterComposite;

public class HtmlScriptLink<T> extends HtmlWriterComposite<T, HtmlScriptLink> {
	
	PrintStream out;

    public HtmlScriptLink(String src) {
        this.addAttr("type", "text/javascript");
        this.addAttr("src", src);
    }

    @Override
    public String getElementName() {
        return ElementType.SCRIPT.toString();
    }
}
