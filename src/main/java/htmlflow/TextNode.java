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

package htmlflow;

import java.io.PrintStream;

/**
 * Represents a leaf of an HTML elements tree. It holds a constant {@link String} or
 * a {@link ModelBinder} that extracts a property value from a domain object.
 *
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class TextNode<T> implements HtmlWriter<T>{

    PrintStream out;
    private final String msg;
    private final ModelBinder<T,?> binder;

    public TextNode(String msg) {
        this.msg = msg;
        this.binder = null;
    }
    public TextNode(ModelBinder<T, ?> binder) {
        this.msg = null;
        this.binder = binder;
    }
    @Override
    public void write(int depth, T model) {
        if(binder == null){
            out.print(msg);
        }
        else{
            assert binder != null;
            out.print(binder.bind(model));
        }
    }
    @Override
    public HtmlWriter<T> setPrintStream(PrintStream out) {
        this.out = out;
        return this;
    }
}
