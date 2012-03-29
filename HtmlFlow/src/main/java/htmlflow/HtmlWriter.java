package htmlflow;

public interface HtmlWriter<T>{
	/**
	 * Writes into an internal Stream the HTML content
	 * if this element.
	 * 
	 * @param depth The number of tabs indentation .
	 * @param model An optional object model that could be bind. 
	 */
	void write(int depth, T model);
}
