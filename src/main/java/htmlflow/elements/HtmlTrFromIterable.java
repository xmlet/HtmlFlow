/*
 * MIT License
 *
 * Copyright (c) 2014-16, Miguel Gamboa (gamboa.pt)
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
package htmlflow.elements;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

import java.io.PrintStream;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlTrFromIterable<S, T extends Iterable<S>> implements HtmlWriter<T>{

    /**
     * @uml.property  name="tr"
     * @uml.associationEnd
     */
    private final HtmlTr<S> tr;

    public HtmlTrFromIterable(ModelBinder<S, ?>[] binders) {
        tr = new HtmlTr<>();
        for (ModelBinder<S, ?> b : binders) {
            tr.td().text(b);
        }
    }

    @Override
    public void write(int depth, T model) {
        for (S item : model) {
            tr.write(depth, item);
        }
    }

    @Override
    public HtmlWriter<T> setPrintStream(PrintStream out) {
        tr.setPrintStream(out);
        return this;
    }
}
