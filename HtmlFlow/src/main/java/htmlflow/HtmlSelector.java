package htmlflow;

/**
 *  Mikael KROK
 *  Allows an element to define its own class and id attribute
 */
public interface HtmlSelector{
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
    void setClassAttribute(String classAttribute);

    /**
     * Set the id attribute
     */
	void setIdAttribute(String idAttribute);
}
