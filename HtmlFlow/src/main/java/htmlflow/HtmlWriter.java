package htmlflow;

import java.io.PrintStream;

public interface HtmlWriter<T>{
	/**
	 * Writes into an internal PrintStream the HTML content
	 * of this element with zero indentation and without a
	 * domain object.
	 */
	default void write() {
		write(0, null);
	}
	/**
	 * Writes into an internal PrintStream the HTML content
	 * of this element with initial indentation of zero.
	 *
	 * @param model An optional object model that could be bind to this element.
	 */
	default void write(T model) {
		write(0, model);
	}
	/**
	 * Writes into an internal PrintStream the HTML content
	 * of this element.
	 * 
	 * @param depth The number of tabs indentation.
	 * @param model An optional object model that could be bind to this element. 
	 */
	void write(int depth, T model);
	
	/**
	 * Sets the current PrintStream.
	 * @param out
	 */
	HtmlWriter<T> setPrintStream(PrintStream out);
}
