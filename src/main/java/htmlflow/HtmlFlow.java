/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt)
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

import htmlflow.visitor.HtmlDocVisitorPrintStream;
import htmlflow.visitor.HtmlDocVisitorStringBuilder;
import htmlflow.visitor.HtmlViewVisitorPrintStream;
import htmlflow.visitor.HtmlViewVisitorStringBuilder;

import java.io.PrintStream;
import java.util.function.BiConsumer;

public class HtmlFlow {

    public static HtmlDoc doc(PrintStream out){
        return out == null
            ? new HtmlDoc(null, new HtmlDocVisitorStringBuilder(true))
            : new HtmlDoc(out, new HtmlDocVisitorPrintStream(out, true));
    }

    public static HtmlDoc doc(){
        return doc(null);
    }

    public static <U> HtmlView<U> view(PrintStream out, HtmlTemplate<U> template){
        return new HtmlView<>(out, (() -> new HtmlViewVisitorPrintStream(out, true)), false, template, null);
    }

    public static <U> HtmlView<U> view(PrintStream out, BiConsumer<HtmlView<U>, U> binder){
        return new HtmlView<>(out, (() -> new HtmlViewVisitorPrintStream(out, true)), false, null, binder);
    }

    public static <U> HtmlView<U> view(HtmlTemplate<U> template){
        return new HtmlView<>(null, (() -> new HtmlViewVisitorStringBuilder(true)), false, template, null);
    }

    public static <U> HtmlView<U> view(BiConsumer<HtmlView<U>, U> binder) {
        return new HtmlView<>(null, (() -> new HtmlViewVisitorStringBuilder(true)), false, null, binder);
    }
}
