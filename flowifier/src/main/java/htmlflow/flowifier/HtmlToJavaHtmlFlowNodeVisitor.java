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

import java.io.IOException;
import java.lang.reflect.Method;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

/**
 * Visitor of a JSoup node that converts the HTML source code into a Java class
 * 
 * @author Julien Gouesse
 *
 * @param <T>
 *            the type of appendable used to store the Java source code
 */
public interface HtmlToJavaHtmlFlowNodeVisitor<T extends Appendable> extends NodeVisitor {

    /**
     * Appends the header of the Java class, i.e the imports, the declaration of
     * the class, the declaration of the method, ...
     * 
     * @throws IOException
     *             thrown when something wrong occurs while appending the Java
     *             source code
     */
    void appendHeader() throws IOException;

    /**
     * Appends the footer of the Java class, i.e closes the method and the class
     * 
     * @throws IOException
     *             thrown when something wrong occurs while appending the Java
     *             source code
     */
    void appendFooter() throws IOException;

    /**
     * Appends the attribute value and key
     * 
     * @param attribute
     *            the attribute
     * @param nodeClass
     *            the class of the node
     * @throws IOException
     *             thrown when something wrong occurs while appending the Java
     *             source code
     */
    void appendAttribute(Attribute attribute, Class<?> nodeClass) throws IOException;

    /**
     * Tells whether a JSoup node cannot be closed
     * 
     * @param node
     *            the JSoup node
     * @return <code>true</code> if the JSoup node cannot be closed, otherwise
     *         <code>false</code>
     */
    boolean isUncloseable(Node node);

    /**
     * Converts the content of a Java string into a string that can be declared
     * in a Java class passed as a method parameter
     * 
     * @param javaStringContent
     *            the content of a Java string
     * @return a string that can be declared in a Java class passed as a method
     *         parameter
     */
    String convertJavaStringContentToJavaDeclarableString(String javaStringContent);

    /**
     * Returns the appendable used to store the Java class
     * 
     * @return the appendable used to store the Java class
     */
    T getAppendable();

    /**
     * Returns the class of the node whose name is passed if any, otherwise
     * <code>null</code>
     * 
     * @param nodeName
     *            the node name
     * @return the class of the node whose name is passed if any, otherwise
     *         <code>null</code>
     */
    Class<?> getClassFromNodeName(String nodeName);

    /**
     * Returns the method of the class node that matches with the attribute if
     * any, otherwise <code>null</code>
     * 
     * @param nodeClass
     *            the class of the node
     * @param attribute
     *            the attribute
     * @return the method of the class node that matches with the attribute if
     *         any, otherwise <code>null</code>
     */
    Method getMethodFromAttribute(Class<?> nodeClass, Attribute attribute);
}
