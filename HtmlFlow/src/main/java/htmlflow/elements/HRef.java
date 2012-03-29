package htmlflow.elements;

import htmlflow.HtmlWriter;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;


public class HRef<T> implements HtmlWriter<T>{
	/*=========================================================================*/
	/*-------------------------     FIELDS    ---------------------------------*/
	/*=========================================================================*/ 

	private final URL url;
	private final PrintStream out; 

	/*=========================================================================*/
	/*-------------------------  CONSTRUCTOR  ---------------------------------*/
	/*=========================================================================*/ 

	public HRef(PrintStream out, String href){
		try {
			url = new URL(href);
			this.out = out;
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

}
