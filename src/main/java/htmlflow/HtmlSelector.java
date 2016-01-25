package htmlflow;

/**
 *  Mikael KROK
 *  Allows an element to define its own class, id attribute and any generic attribute
 *  @param <T> The type derived of HtmlSelector
 */
public interface HtmlSelector<T>{
    /**
     * Get the class attribute if the object has one
     * @return
     */
    String getClassAttribute();

    /**
     * Get the id attribute if the object has one
	 */
	String getIdAttribute();

    /**
     * Set the class attribute
     * @return
     */
    T classAttr(String classAttribute);

    /**
     * Set the id attribute
     */
	T idAttr(String idAttribute);
}
