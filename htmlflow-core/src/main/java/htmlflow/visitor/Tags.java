/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
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

    private Tags() {}

    /** Write {@code "<elementName"}. */
    public static void beginTag(Appendable out, String elementName) {
        try {
            out.append(BEGIN_TAG);
            out.append(elementName);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /** Writes {@code "</elementName>"}. */
    public static void endTag(Appendable out, String elementName) {
        try {
            out.append(BEGIN_CLOSE_TAG); // </
            out.append(elementName); // </name
            out.append(FINISH_TAG); // </name>
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /** Writes {@code "attributeName=attributeValue"} */
    public static void addAttribute(
        Appendable out,
        String attributeName,
        String attributeValue
    ) {
        try {
            out.append(SPACE);
            out.append(attributeName);
            out.append(ATTRIBUTE_MID);
            out.append(attributeValue);
            out.append(QUOTATION);
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }

    /** Writes {@code "<!--s-->"} */
    public static void addComment(Appendable out, String comment) {
        try {
            out.append(BEGIN_COMMENT_TAG); // <!--
            out.append(comment);
            out.append(END_COMMENT_TAG); // -->
        } catch (IOException e) {
            throw new HtmlFlowAppendException(e);
        }
    }
}
