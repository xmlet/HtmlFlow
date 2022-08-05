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
import htmlflow.visitor.HtmlVisitorPrintStreamDynamic;
import htmlflow.visitor.HtmlVisitorStringBuilder;

import java.io.PrintStream;
import java.util.function.Supplier;

/**
 * Static Html view.
 *
 * @author Miguel Gamboa, Luís Duare
 */
public class StaticHtml extends AbstractHtmlWriter<Object> {

    private static final String WRONG_USE_OF_RENDER_WITH_MODEL =
             "Wrong use of StaticView! Model object not " +
             "supported or you should use a dynamic view instead!";
    private static final String WRONG_USE_OF_WRITE_FOR_VISITOR_STRING_BUILDER =
            "Use render() instead of write(). This HtmlDoc has already a " +
            "HtmlVisitorStringBuilder that collects emitted HTML.";


    private final PrintStream out;

    public static StaticHtml view(PrintStream out){
        return out == null ? view() : new StaticHtml(out);
    }

    public static StaticHtml view(){
        return new StaticHtml();
    }

    /**
     * Auxiliary constructor used by clone().
     */
    private StaticHtml(
        Supplier<HtmlVisitor> visitorSupplier,
        boolean threadSafe,
        PrintStream out)
    {
        super(visitorSupplier, threadSafe);
        this.out = out;
    }

    private StaticHtml() {
        super(() -> new HtmlVisitorStringBuilder(true), false);
        this.out = null;
    }

    private StaticHtml(PrintStream out) {
        super(() -> new HtmlVisitorPrintStreamDynamic(out, true), false);
        this.out = out;
    }

    @Override
    public final String render() {
        return getVisitor().finished();
    }

    @Override
    public final String render(Object model) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITH_MODEL);
    }

    @Override
    public final void write() {
        throw new UnsupportedOperationException(WRONG_USE_OF_WRITE_FOR_VISITOR_STRING_BUILDER);
    }

    @Override
    public final void write(Object model) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITH_MODEL);
    }

    /**
     * Since HtmlView is immutable this is the preferred way to create a copy of the
     * existing HtmlView instance with a different threadSafe state.
     *
     * @param visitorSupplier
     * @param threadSafe
     */
    @Override
    protected final AbstractHtmlWriter<Object> clone(Supplier<HtmlVisitor> visitorSupplier, boolean threadSafe) {
        return new StaticHtml(visitorSupplier, threadSafe, out);
    }

    /**
     * Resulting in a non thread safe view.
     * Receives an existent visitor.
     * Usually for a parent view to share its visitor with a partial.
     */
    @Override
    protected AbstractHtmlWriter<Object> clone(HtmlVisitor visitor) {
        return new StaticHtml(() -> visitor, false, out);
    }

    @Override
    public String getName() {
        return "HtmlDoc";
    }
}
