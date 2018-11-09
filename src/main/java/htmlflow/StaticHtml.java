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

import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Static Html view.
 *
 * @author Miguel Gamboa, Luís Duare
 */
public class StaticHtml extends HtmlView<Object> {

    private static final String WRONG_USE_OF_RENDER_WITH_MODEL =
             "Wrong use of StaticView! Model object not " +
             "supported or you should use a dynamic view instead!";

    private final Consumer<StaticHtml> template;

    public static StaticHtml view(PrintStream out, Consumer<StaticHtml> template){
        return new StaticHtml(out, template);
    }

    public static StaticHtml view(PrintStream out){
        return new StaticHtml(out);
    }

    public static StaticHtml view(Consumer<StaticHtml> template){
        return new StaticHtml(template);
    }

    public static StaticHtml view(){
        return new StaticHtml();
    }

    /**
     * Auxiliary constructor used by clone().
     */
    private StaticHtml(
        Supplier<HtmlVisitorCache> visitorSupplier,
        boolean threadSafe,
        Consumer<StaticHtml> template)
    {
        super(visitorSupplier, threadSafe);
        this.template = template;
    }

    private StaticHtml() {
        this((Consumer<StaticHtml>) null);
    }

    private StaticHtml(Consumer<StaticHtml>  template) {
        super(() -> new HtmlVisitorStringBuilder(false), false);
        this.template = template;
    }

    private StaticHtml(PrintStream out) {
        this(out, null);
    }

    private StaticHtml(PrintStream out, Consumer<StaticHtml> template) {
        super(() -> new HtmlVisitorPrintStream(out, false), false);
        this.template = template;
    }

    @Override
    public final String render() {
        if(template != null)
            template.accept(this);
        return getVisitor().finished();

    }

    @Override
    public final String render(Object model) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITH_MODEL);
    }

    @Override
    public final void write() {
        if(template != null)
            template.accept(this);
        getVisitor().finished();
    }

    @Override
    public final void write(Object model) {
        throw new UnsupportedOperationException(WRONG_USE_OF_RENDER_WITH_MODEL);
    }

    @Override
    protected final HtmlView<Object> clone(Supplier<HtmlVisitorCache> visitorSupplier, boolean threadSafe) {
        return new StaticHtml(visitorSupplier, threadSafe, template);
    }
}
