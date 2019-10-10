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
package htmlflow.flowifier;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class Flowifier {
	
	public Flowifier() {
		super();
	}
	
	public <T extends Appendable> T toFlow(final Node node, final HtmlToJavaHtmlFlowNodeVisitor<T> htmlToJavaHtmlFlowNodeVisitor) {
		node.traverse(htmlToJavaHtmlFlowNodeVisitor);
		final T result = htmlToJavaHtmlFlowNodeVisitor.getAppendable();
		return result;
	}
	
	public <T extends Appendable> T toFlow(final Document doc, final HtmlToJavaHtmlFlowNodeVisitor<T> htmlToJavaHtmlFlowNodeVisitor) {
		return toFlow(doc.root(), htmlToJavaHtmlFlowNodeVisitor);
	}
	
	public String toFlow(final Document doc) {
		final DefaultHtmlToJavaHtmlFlowNodeVisitor visitor = new DefaultHtmlToJavaHtmlFlowNodeVisitor();
		final StringBuilder builder = toFlow(doc, visitor);
		return builder.toString();
	}
	
	public <T extends Appendable> T toFlow(final String url, final HtmlToJavaHtmlFlowNodeVisitor<T> htmlToJavaHtmlFlowNodeVisitor) throws IOException {
		final Document doc = Jsoup.connect(url).get();
		return toFlow(doc, htmlToJavaHtmlFlowNodeVisitor);
	}
	
	public String toFlow(final String url) throws IOException {
		final DefaultHtmlToJavaHtmlFlowNodeVisitor visitor = new DefaultHtmlToJavaHtmlFlowNodeVisitor();
		final StringBuilder builder = toFlow(url, visitor);
		return builder.toString();
	}
}
