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

import java.io.PrintStream;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlFormSelect implements HtmlWriter<Object>{
    PrintStream out;
    final String name;
    final String[] options;

    public HtmlFormSelect(String name, String...options) {
        this.name = name;
        this.options = options;
    }
    @Override
    public void write(int depth, Object model) {
        out.println("<select name=\""+ name+ "\">");
        tabs(depth + 1);
        for (String op : options) {
            out.println("<option>" + op + "</option>");
            tabs(depth);
        }
        tabs(depth - 1);
        out.println("</select>");
        tabs(depth);
    }

    @Override
    public HtmlWriter<Object> setPrintStream(PrintStream out) {
        this.out = out;
        return this;
    }

    /*=========================================================================*/
    /*-------------------- auxiliar Methods ----------------------------*/
    /*=========================================================================*/

    public final void tabs(int depth){
        for (int i = 0; i < depth; i++) out.print("\t");
    }
}
