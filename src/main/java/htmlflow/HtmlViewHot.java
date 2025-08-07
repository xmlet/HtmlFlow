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

import htmlflow.visitor.HtmlVisitor;
import org.xmlet.htmlapifaster.Html;

import java.util.function.Supplier;

/**
 * Dynamic views can be bound to a Model object and unoptimized.
 * It does not store static HTML blocks and recalculate HTML on every rendering.
 *
 * @param <M> Type of the model rendered with this view.
 *
 * @author Miguel Gamboa
 */
public class HtmlViewHot<M> extends HtmlView<M> {

    /**
     * Auxiliary constructor used by clone().
     *
     * @param visitorSupplier
     * @param template
     * @param threadSafe
     */
    HtmlViewHot(Supplier<HtmlVisitor> visitorSupplier, HtmlTemplate template, boolean threadSafe) {
        super(visitorSupplier, template, threadSafe);
    }

    public String render() {
        return render(null);
    }

    public String render(M model) {
        HtmlVisitor visitor = getVisitor();
        StringBuilder str = ((StringBuilder) visitor.out());
        str.setLength(0); // clear buffer
        visitor.resolve(model); // passes the model to the Visitor to then be passed to the dynamic blocks
        this.template.resolve(this); // processing template which emits HTML
        return str.toString();
    }

    public void write(M model) {
        HtmlVisitor visitor = getVisitor();
        visitor.resolve(model); // passes the model to the Visitor to then be passed to the dynamic blocks
        this.template.resolve(this); // processing template which emits HTML
    }

    public void write() {
        write(null);
    }

}
