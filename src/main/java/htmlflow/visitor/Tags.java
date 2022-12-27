package htmlflow.visitor;

import htmlflow.exceptions.HtmlFlowAppendException;

import java.io.IOException;

public class Tags {
    static final char BEGIN_TAG = '<';
    static final String BEGIN_CLOSE_TAG = "</";
    static final String BEGIN_COMMENT_TAG = "<!-- ";
    static final String END_COMMENT_TAG = " -->";
    static final String ATTRIBUTE_MID = "=\"";
    static final char FINISH_TAG = '>';
    static final char SPACE = ' ';
    static final char QUOTATION = '"';

    private Tags() {
    }
    /**
     * Write {@code "<elementName"}.
     */
    public static void beginTag(Appendable out, String elementName) {
        try {
            out.append(BEGIN_TAG);
            out.append(elementName);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Writes {@code "</elementName>"}.
     */
    public static void endTag(Appendable out, String elementName) {
        try {
            out.append(BEGIN_CLOSE_TAG);     // </
            out.append(elementName);         // </name
            out.append(FINISH_TAG);          // </name>
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Writes {@code "attributeName=attributeValue"}
     */
    public static void addAttribute(Appendable out, String attributeName, String attributeValue)  {
        try {
            out.append(SPACE);
            out.append(attributeName);
            out.append(ATTRIBUTE_MID);
            out.append(attributeValue);
            out.append(QUOTATION);
        }catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /**
     * Writes {@code "<!--s-->"}
     */
    public static void addComment(Appendable out, String comment) {
        try {
            out.append(BEGIN_COMMENT_TAG);   // <!--
            out.append(comment);
            out.append(END_COMMENT_TAG);     // -->
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }
}
