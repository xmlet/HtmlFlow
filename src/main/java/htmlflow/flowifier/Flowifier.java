/*
 * MIT License
 *
 * Copyright (c) 2014-19 HtmlFlow.org
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
package htmlflow.flowifier;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.IOException;

/**
 * Main class of the flowifier used to convert a whole document or the content
 * of a node into a Java class
 * 
 * @author Julien Gouesse
 *
 */
public class Flowifier {

    /**
     * Constructor
     */
    public Flowifier() {
        super();
    }

    /**
     * Returns the Java source code of a class that generates the HTML source
     * code of the given node
     * 
     * @param <T>
     *            the type of appendable used to store the Java source code
     * @param node
     *            the node whose content has to be converted
     * @param htmlToJavaHtmlFlowNodeVisitor
     *            the visitor that performs the conversion
     * @return the Java source code of a class that generates the HTML source
     *         code of the given node
     */
    public <T extends Appendable> T toFlow(final Node node,
            final HtmlToJavaHtmlFlowNodeVisitor<T> htmlToJavaHtmlFlowNodeVisitor) {
        node.traverse(htmlToJavaHtmlFlowNodeVisitor);
        return htmlToJavaHtmlFlowNodeVisitor.getAppendable();
    }

    /**
     * Returns the Java source code of a class that generates the HTML source
     * code of the given document
     * 
     * @param <T>
     *            the type of appendable used to store the Java source code
     * @param doc
     *            the document whose content has to be converted
     * @param htmlToJavaHtmlFlowNodeVisitor
     *            the visitor that performs the conversion
     * @return the Java source code of a class that generates the HTML source
     *         code of the given document
     */
    public <T extends Appendable> T toFlow(final Document doc,
            final HtmlToJavaHtmlFlowNodeVisitor<T> htmlToJavaHtmlFlowNodeVisitor) {
        return toFlow(doc.root(), htmlToJavaHtmlFlowNodeVisitor);
    }

    /**
     * Returns the Java source code of a class that generates the HTML source
     * code of the given document
     * 
     * @param doc
     *            the document whose content has to be converted
     * @return the Java source code of a class that generates the HTML source
     *         code of the given document
     */
    public String toFlow(final Document doc) {
        final DefaultHtmlToJavaHtmlFlowNodeVisitor visitor = new DefaultHtmlToJavaHtmlFlowNodeVisitor();
        final StringBuilder builder = toFlow(doc, visitor);
        return builder.toString();
    }

    /**
     * Returns the Java source code of a class that generates the HTML source
     * code of the document at the given URL
     * 
     * @param <T>
     *            the type of appendable used to store the Java source code
     * @param url
     *            the URL of the document
     * @param htmlToJavaHtmlFlowNodeVisitor
     *            the visitor that performs the conversion
     * @return the Java source code of a class that generates the HTML source
     *         code of the document at the given URL
     * @throws IOException
     *             thrown when something wrong occurs while getting the document
     *             at the given URL
     */
    public <T extends Appendable> T toFlow(final String url,
            final HtmlToJavaHtmlFlowNodeVisitor<T> htmlToJavaHtmlFlowNodeVisitor) throws IOException {
        final Document doc = Jsoup.connect(url).get();
        return toFlow(doc, htmlToJavaHtmlFlowNodeVisitor);
    }

    /**
     * Returns the Java source code of a class that generates the HTML source
     * code of the document at the given URL
     * 
     * @param url
     *            the URL of the document
     * @return the Java source code of a class that generates the HTML source
     *         code of the document at the given URL
     * @throws IOException
     *             thrown when something wrong occurs while getting the document
     *             at the given URL
     */
    public String toFlow(final String url) throws IOException {
        final DefaultHtmlToJavaHtmlFlowNodeVisitor visitor = new DefaultHtmlToJavaHtmlFlowNodeVisitor();
        final StringBuilder builder = toFlow(url, visitor);
        return builder.toString();
    }

    /**
     * Returns the Java source code of a class that generates the HTML source
     * code of the given source.
     *
     * @param src
     *            the HTML whose content has to be converted
     * @return the Java source code of a class that generates the HTML source
     *         code of the given source.
     */
    public static String fromHtml(final String src) {
        Flowifier flw = new Flowifier();
        return flw.toFlow(Jsoup.parse(src));
    }
}
