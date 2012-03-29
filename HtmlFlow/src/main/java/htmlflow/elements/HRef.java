package htmlflow.elements;

import htmlflow.HtmlWriter;

import java.net.MalformedURLException;
import java.net.URL;


public class HRef<T> implements HtmlWriter<T>{
	private final URL url;
	
	public HRef(String href){
		try {
			url = new URL(href);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String write(int depth, T model) {
		return url.toExternalForm();
	}

}
