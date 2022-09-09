package htmlflow.visitor;

public interface Tags {
    char BEGIN_TAG = '<';
    String BEGIN_CLOSE_TAG = "</";
    String BEGIN_COMMENT_TAG = "<!-- ";
    String END_COMMENT_TAG = " -->";
    String ATTRIBUTE_MID = "=\"";
    char FINISH_TAG = '>';
    char SPACE = ' ';
    char QUOTATION = '"';

    /**
     * Write {@code "<elementName"}.
     */
    void beginTag(String elementName);

    /**
     * Writes {@code "</elementName>"}.
     */
    void endTag(String elementName);

    /**
     * Writes {@code "attributeName=attributeValue"}
     */
    void addAttribute(String attributeName, String attributeValue);

    /**
     * Writes {@code "<!--s-->"}
     */
    void addComment(String s);
}
