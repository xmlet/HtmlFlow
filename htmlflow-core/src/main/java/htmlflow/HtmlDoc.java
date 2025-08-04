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

import htmlflow.visitor.HtmlDocVisitor;
import org.xmlet.htmlapifaster.Html;

/**
 * Static Html view.
 *
 * @author Miguel Gamboa, Luís Duare
 */
public class HtmlDoc extends HtmlPage {

    private final HtmlDocVisitor visitor;

    HtmlDoc(HtmlDocVisitor visitor) {
        this.visitor = visitor;
    }

    public final Html<HtmlPage> html() {
        this.getVisitor().write(HEADER);
        return new Html<>(this);
    }

    @Override
    public String getName() {
        return "HtmlDoc";
    }

    @Override
    public HtmlPage setIndented(boolean isIndented) {
        return new HtmlDoc(getVisitor().clone(isIndented));
    }

    @Override
    public HtmlPage threadSafe() {
        throw new IllegalStateException("HtmlDoc is not reusable and does not keep internal static blocks!" +
         "Thus it does not require thread safety!");
    }

    @Override
    public HtmlDocVisitor getVisitor() {
        return visitor;
    }
}
