package htmlflow.elements;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import htmlflow.HtmlWriter;


public class HRef<T> implements HtmlWriter<T>{
	/*=========================================================================*/
	/*-------------------------     FIELDS    ---------------------------------*/
	/*=========================================================================*/ 

	private final URL url;
	private PrintStream out; 

	/*=========================================================================*/
	/*-------------------------  CONSTRUCTOR  ---------------------------------*/
	/*=========================================================================*/ 

	public HRef(String href){
		try {
			url = new URL(href);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*=========================================================================*/
	/*--------------------- HtmlPrinter interface -----------------------------*/
	/*=========================================================================*/ 

	@Override
	public void write(int depth, T model) {
		out.print(url.toExternalForm());
	}

	@Override
	public HtmlWriter<T> setPrintStream(PrintStream out) {
		this.out = out;
		return this;
	}
}
