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

/**
 * Default implementation of the visitor of a JSoup node that converts the HTML
 * source code into a Java class, it uses a StringBuilder to append the content
 * of the Java class. Note that this implementation is not intended to be
 * thread-safe
 * 
 * @author Julien Gouesse
 *
 */
public class DefaultHtmlToJavaHtmlFlowNodeVisitor extends AbstractHtmlToJavaHtmlFlowNodeVisitor<StringBuilder> {

    /**
     * Constructor with indentation disabled
     */
    public DefaultHtmlToJavaHtmlFlowNodeVisitor() {
        this(false);
    }

    /**
     * Constructor
     * 
     * @param indented
     *            <code>true</code> if the generated HTML source code is
     *            indented, otherwise <code>false</code>
     */
    public DefaultHtmlToJavaHtmlFlowNodeVisitor(final boolean indented) {
        super(StringBuilder::new, indented);
    }
}
