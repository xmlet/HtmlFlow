/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
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

import java.io.PrintStream;

/**
 * @author Miguel Gamboa, Luís Duare
 */
interface TagsToPrintStream extends Tags {

    PrintStream out();

    default void beginTag(String elementName) {
        out().print(BEGIN_TAG);
        out().print(elementName);
    }

    default void addAttribute(String attributeName, String attributeValue)  {
        out().print(SPACE);
        out().print(attributeName);
        out().print(ATTRIBUTE_MID);
        out().print(attributeValue);
        out().print(QUOTATION);
    }

    default void addComment(String comment) {
        out().print(BEGIN_COMMENT_TAG);
        out().print(comment);
        out().print(END_COMMENT_TAG);
    }

    default void endTag(String elementName) {
        out().print(BEGIN_CLOSE_TAG);     // </
        out().print(elementName);         // </name
        out().print(FINISH_TAG);          // </name>
    }

    static void appendOpenTag(StringBuilder sb, String elementName) {
        sb.append(BEGIN_TAG);
        sb.append(elementName);
    }

    static void appendAttribute(StringBuilder sb, String attributeName, String attributeValue) {
        sb.append(SPACE);
        sb.append(attributeName);
        sb.append(ATTRIBUTE_MID);
        sb.append(attributeValue);
        sb.append(QUOTATION);
    }

    static void appendComment(StringBuilder sb, String comment) {
        sb.append(BEGIN_COMMENT_TAG);   // <!--
        sb.append(comment);
        sb.append(END_COMMENT_TAG);     // -->
    }

    static void appendCloseTag(StringBuilder sb, String elementName) {
        sb.append(BEGIN_CLOSE_TAG);     // </
        sb.append(elementName);         // </name
        sb.append(FINISH_TAG);          // </name>
    }
}
