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
 * This is the implementation of the ElementVisitor (from HtmlApiFaster
 * library) which uses an internal StringBuilder to collect information
 * about visited Html elements of a HtmlView.
 *
 * @author Miguel Gamboa, Luís Duare
 *         created on 17-01-2018
 */
public class HtmlViewVisitorStringBuilder extends HtmlViewVisitor implements TagsToStringBuilder {
    /**
     * The main StringBuilder. Read by the finished() to return the
     * resulting string with the Html content.
     */
    private final StringBuilder sb = new StringBuilder();
    

    public HtmlViewVisitorStringBuilder(boolean isIndented) {
        super(isIndented);
    }

    public HtmlViewVisitorStringBuilder(boolean isIndented, int depth) {
        this(isIndented);
        this.depth = depth;
    }

    /**
     * Creates a new similar instance with all static bocks cleared.
     */
    @Override
    public HtmlViewVisitor newbie() {
        return new HtmlViewVisitorStringBuilder(isIndented, depth);
    }

    @Override
    public void write(String text) {
        sb.append(text);
    }
    @Override
    protected void write(char c) {
        sb.append(c);
    }

    @Override
    protected String substring(int staticBlockIndex) {
        return sb.substring(staticBlockIndex);
    }

    @Override
    protected int size() {
        return sb.length();
    }

    @Override
    protected String readAndReset() {
        String data = sb.toString();
        sb.setLength(0);
        return data;
    }

    @Override
    public HtmlViewVisitor clone(PrintStream out, boolean isIndented) {
        if(out != null)
            throw new IllegalArgumentException("This HtmlVisitor emits to StringBuilder and does not support PrintStream!");
        return new HtmlViewVisitorStringBuilder(isIndented);
    }

    @Override
    public StringBuilder sb() {
        return sb;
    }
}
