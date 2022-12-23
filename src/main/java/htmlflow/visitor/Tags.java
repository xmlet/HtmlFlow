package htmlflow.visitor;

import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;

public interface Tags {
    char BEGIN_TAG = '<';
    String BEGIN_CLOSE_TAG = "</";
    String BEGIN_COMMENT_TAG = "<!-- ";
    String END_COMMENT_TAG = " -->";
    String ATTRIBUTE_MID = "=\"";
    char FINISH_TAG = '>';
    char SPACE = ' ';
    char QUOTATION = '"';

    Appendable out();

    /**
     * Write {@code "<elementName"}.
     */
    default void beginTag(String elementName) {
        try {
            out().append(BEGIN_TAG);
            out().append(elementName);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Writes {@code "</elementName>"}.
     */
    default void endTag(String elementName) {
        try {
            out().append(BEGIN_CLOSE_TAG);     // </
            out().append(elementName);         // </name
            out().append(FINISH_TAG);          // </name>
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Writes {@code "attributeName=attributeValue"}
     */
    default void addAttribute(String attributeName, String attributeValue)  {
        try {
            out().append(SPACE);
            out().append(attributeName);
            out().append(ATTRIBUTE_MID);
            out().append(attributeValue);
            out().append(QUOTATION);
        }catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Writes {@code "<!--s-->"}
     */
    default void addComment(String comment) {
        try {
            out().append(BEGIN_COMMENT_TAG);   // <!--
            out().append(comment);
            out().append(END_COMMENT_TAG);     // -->
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }
}
