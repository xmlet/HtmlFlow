/*
 * MIT License
 *
 * Copyright (c) 2014-16, mcarvalho (gamboa.pt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow;

import org.xmlet.htmlapifaster.*;

import java.io.PrintStream;

public class HtmlTags {
    static final char BEGIN_TAG = '<';
    static final String BEGIN_CLOSE_TAG = "</";
    static final String BEGIN_COMMENT_TAG = "<!-- ";
    static final String END_COMMENT_TAG = " -->";
    static final String ATTRIBUTE_MID = "=\"";
    static final char FINISH_TAG = '>';
    static final char SPACE = ' ';
    static final char QUOTATION = '"';
    static final char EQUALS = '=';
    static final char SLASH = '/';

    private HtmlTags() {
    }

    /**
     * According to https://www.w3.org/TR/html5/syntax.html#void-elements
     */
    static final String[] VOID_ELEMENTS = {
            firstToLower(Area.class.getSimpleName()),
            firstToLower(Base.class.getSimpleName()),
            firstToLower(Br.class.getSimpleName()),
            firstToLower(Col.class.getSimpleName()),
            firstToLower(Embed.class.getSimpleName()),
            firstToLower(Hr.class.getSimpleName()),
            firstToLower(Img.class.getSimpleName()),
            firstToLower(Input.class.getSimpleName()),
            firstToLower(Link.class.getSimpleName()),
            firstToLower(Meta.class.getSimpleName()),
            firstToLower(Param.class.getSimpleName()),
            firstToLower(Source.class.getSimpleName())
    };

    static String firstToLower(String name){
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    static void printOpenTag(PrintStream out, String elementName) {
        out.print(BEGIN_TAG);
        out.print(elementName);
    }

    public static void printAttribute(PrintStream out, String attributeName, String attributeValue) {
        out.print(SPACE);
        out.print(attributeName);
        out.print(ATTRIBUTE_MID);
        out.print(attributeValue);
        out.print(QUOTATION);
    }

    static void printOpenTagEnd(PrintStream out) {
        out.print(FINISH_TAG);
    }

    static void printOpenComment(PrintStream out) {
        out.print(BEGIN_COMMENT_TAG);
    }

    static void printEndComment(PrintStream out) {
        out.print(END_COMMENT_TAG);
    }

    static void printCloseTag(PrintStream out, String elementName) {
        out.print(BEGIN_CLOSE_TAG);     // </
        out.print(elementName);         // </name
        out.print(FINISH_TAG);          // </name>
    }

    public static void appendOpenTag(StringBuilder sb, String elementName) {
        sb.append(BEGIN_TAG);
        sb.append(elementName);
    }

    public static void appendOpenTag2(StringBuilder sb, String openingTag) {
        sb.append(openingTag);
    }

    public static void appendAttribute(StringBuilder sb, String attributeName, String attributeValue) {
        sb.append(SPACE);
        sb.append(attributeName);
        sb.append(ATTRIBUTE_MID);
        sb.append(attributeValue);
        sb.append(QUOTATION);
    }

    public static void appendAttribute2(StringBuilder sb, String firstBlock, String attributeValue) {
        sb.append(firstBlock); // " attributeName=\""
        sb.append(attributeValue);
        sb.append(QUOTATION);
    }

    public static void appendOpenTagEnd(StringBuilder sb) {
        sb.append(FINISH_TAG);
    }


    public static void appendCloseTag(StringBuilder sb, String elementName) {
        sb.append(BEGIN_CLOSE_TAG);     // </
        sb.append(elementName);         // </name
        sb.append(FINISH_TAG);          // </name>
    }

    public static void appendCloseTag2(StringBuilder sb, String closingTag) {
        sb.append(closingTag);          // </name>
    }

    public static void appendOpenComment(StringBuilder sb) {
        sb.append(BEGIN_COMMENT_TAG);      // <!--
    }

    public static void appendEndComment(StringBuilder sb) {
        sb.append(END_COMMENT_TAG);      // -->
    }

    static boolean isVoidElement(String elementName) {
        for (int i = 0; i < VOID_ELEMENTS.length; i++) {
            if(VOID_ELEMENTS[i].equals(elementName))
                return true;
        }
        return false;
    }

}
