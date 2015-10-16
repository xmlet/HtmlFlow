package htmlflow.elements;

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.HtmlWriterComposite;

public class HtmlLinkCss<T> extends HtmlWriterComposite<T, HtmlLinkCss> {
	PrintStream out;

	public HtmlLinkCss(String href) {
        this.addAttr("rel", "Stylesheet");
        this.addAttr("type", "\"text/css\"");
        this.addAttr("href", href);
	}

}
