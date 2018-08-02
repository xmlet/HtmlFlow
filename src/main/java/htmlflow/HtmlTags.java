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

import org.xmlet.htmlapi.Area;
import org.xmlet.htmlapi.Attribute;
import org.xmlet.htmlapi.Base;
import org.xmlet.htmlapi.Br;
import org.xmlet.htmlapi.Col;
import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.Embed;
import org.xmlet.htmlapi.Hr;
import org.xmlet.htmlapi.Img;
import org.xmlet.htmlapi.Input;
import org.xmlet.htmlapi.Link;
import org.xmlet.htmlapi.Meta;
import org.xmlet.htmlapi.Param;
import org.xmlet.htmlapi.Source;

import java.io.PrintStream;

public class HtmlTags {
    static final char BEGIN_TAG = '<';
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
    static final Class<?>[] VOID_ELEMENTS = {
            Area.class,
            Base.class,
            Br.class,
            Col.class,
            Embed.class,
            Hr.class,
            Img.class,
            Input.class,
            Link.class,
            Meta.class,
            Param.class,
            Source.class
    };

    static void printOpenTag(PrintStream out, Element<?, ?> elem) {
        out.print(BEGIN_TAG);
        out.print(elem.getName());
        for (Attribute attribute : elem.getAttributes()) {
            out.print(SPACE);
            out.print(attribute.getName());
            out.print(EQUALS);
            out.print(QUOTATION);
            out.print(attribute.getValue());
            out.print(QUOTATION);
        }
        out.print(FINISH_TAG);
    }

    static void printCloseTag(PrintStream out, Element<?, ?> elem) {
        out.print(BEGIN_TAG);      // <
        out.print(SLASH);          // </
        out.print(elem.getName()); // </name
        out.print(FINISH_TAG);     // </name>
    }

    public static void appendOpenTag(StringBuilder sb, Element<?, ?> elem) {
        sb.append(BEGIN_TAG);
        sb.append(elem.getName());
        for (Attribute attribute : elem.getAttributes()) {
            sb.append(SPACE);
            sb.append(attribute.getName());
            sb.append(EQUALS);
            sb.append(QUOTATION);
            sb.append(attribute.getValue());
            sb.append(QUOTATION);
        }
        sb.append(FINISH_TAG);
    }

    public static void appendCloseTag(StringBuilder sb, Element<?, ?> elem) {
        sb.append(BEGIN_TAG);      // <
        sb.append(SLASH);          // </
        sb.append(elem.getName()); // </name
        sb.append(FINISH_TAG);     // </name>
    }

    static boolean isVoidElement(Element elem) {
        Class<? extends Element> k = elem.getClass();
        for (int i = 0; i < VOID_ELEMENTS.length; i++) {
            if(VOID_ELEMENTS[i] == k)
                return true;
        }
        return false;
    }

}
