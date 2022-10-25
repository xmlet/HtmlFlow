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
 * @param <T> The type of domain object (i.e. the model)
 * @author Miguel Gamboa, Luís Duare
 * created on 17-01-2018
 */
public class HtmlViewVisitorPrintStream<T> extends HtmlViewVisitorContinuations<T> implements TagsToPrintStream {
    /**
     * The final PrintStream destination of the HTML content
     * produced by this visitor.
     */
    private final PrintStream out;

    public HtmlViewVisitorPrintStream(PrintStream out, boolean isIndented, HtmlContinuation<Object> first)
    {
        super(isIndented, first);
        this.out = out;
    }

    public HtmlViewVisitorPrintStream(
        PrintStream out,
        boolean isIndented,
        int depth,
        HtmlContinuation<Object> first)
    {
        this(out, isIndented, first);
        this.depth = depth;
    }

    @Override
    public void write(String text) {
        out.print(text);
    }

    @Override
    protected void write(char c) {
        out.print(c);
    }

    @Override
    protected String readAndReset() {
        /**
         * This visitor writes the content to a PrintStream and we should not consider
         * the value returned by readAndReset().
         * For that reason we return null to avoid misleading uses of this result.
         * For anyone who tries to use it will get NullPointerException.
         */
        return null;
    }

    @Override
    public HtmlViewVisitor clone(PrintStream out, boolean isIndented) {
        return new HtmlViewVisitorPrintStream(out, isIndented, first);
    }

    @Override
    public PrintStream out() {
        return out;
    }
}
