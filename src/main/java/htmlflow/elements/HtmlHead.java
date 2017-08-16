/*
 * MIT License
 * Copyright (c) 2014-16, Miguel Gamboa (gamboa.pt)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE
 * SOFTWARE.
 */
package htmlflow.elements;

import htmlflow.HtmlWriterComposite;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlHead<T> extends HtmlWriterComposite<T, HtmlHead<T>> {
    
    public HtmlHead<T> title(final String msg) {
        this.addChild(new HtmlTitle<T>()).text(msg);
        return this;
    }
    
    public HtmlHead<T> scriptLink(final String src) {
        this.addChild(new HtmlScriptLink<T>(src));
        return this;
    }
    
    public HtmlScriptBlock<T> scriptBlock() {
        return this.addChild(new HtmlScriptBlock<T>());
    }
    
    public HtmlHead<T> linkCss(final String href) {
        this.addChild(new HtmlLinkCss<T>(href));
        return this;
    }
    
    public HtmlHead<T> meta(final String attr, final String value) {
        this.addChild(new HtmlMeta<T>().addAttr(attr, value));
        return this;
    }
    
    @Override
    public String getElementName() {
        return ElementType.HEAD.toString();
    }
}
