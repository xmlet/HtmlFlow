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

package htmlflow;

import htmlflow.util.PrintStringBuilder;

import java.io.PrintStream;

/**
 * @author Miguel Gamboa, Luís Duare
 *         created on 17-01-2018
 */
public class HtmlVisitorPrintStream extends HtmlVisitorCache {
    /**
     * The final PrintStream destination of the HTML content
     * produced by this visitor.
     */
    private final PrintStream out;
    /**
     * This is a PrintStringBuilder which collects all content into
     * an internal StringBuilder, which is read by the cache.
     * When first visit finishes we exchange current to the value of
     * field out, which is a PrintStream.
     */
    private PrintStream current;

    /**
     * Set HTML output indentation with true by default.
     */
    public HtmlVisitorPrintStream(PrintStream out, boolean isDynamic) {
        this(out, isDynamic, true);
    }

    public HtmlVisitorPrintStream(PrintStream out, boolean isDynamic, boolean isIndented) {
        super(isDynamic, isIndented);
        this.out = out;
        this.current = new PrintStringBuilder(out);
    }

    @Override
    protected void beginTag(String elementName) {
        Tags.printOpenTag(current, elementName); // "<elementName"
    }

    @Override
    protected void endTag(String elementName) {
        Tags.printCloseTag(current, elementName); // </elementName>
    }

    @Override
    protected void addAttribute(String attributeName, String attributeValue) {
        Tags.printAttribute(current, attributeName, attributeValue);
    }

    @Override
    protected void addComment(String comment) {
        Tags.printComment(current, comment);
    }

    @Override
    protected void write(String text) {
        current.print(text);
    }
    @Override
    protected void write(char c) {
        current.print(c);
    }

    @Override
    protected String substring(int staticBlockIndex) {
        /**
         * REMARK: we need to keep current field of type PrintStream because after
         * the first visit we exchange it to the value of field out, which is a PrintStream.
         * After that, the cache is finished and we are sure that substring() is no
         * longer invoked.
         */
        return ((PrintStringBuilder) current).substring(staticBlockIndex);
    }

    @Override
    protected int size() {
        /**
         * REMARK: we need to keep current field of type PrintStream because after
         * the first visit we exchange it to the value of field out, which is a PrintStream.
         * After that, the cache is finished and we are sure that size() is no
         * longer invoked.
         */
        return ((PrintStringBuilder) current).length();
    }

    @Override
    protected String readAndReset() {
        this.current = out;
        /**
         * This visitor writes the content to a PrintStream and we should not consider
         * the value returned by readAndReset().
         * For that reason we return null to avoid misleading uses of this result.
         * For anyone who tries to use it will get NullPointerException.
         */
        return null;
    }

    @Override
    protected HtmlVisitorCache clone(boolean isIndented) {
        return new HtmlVisitorPrintStream(out, isDynamic, isIndented);
    }
}
