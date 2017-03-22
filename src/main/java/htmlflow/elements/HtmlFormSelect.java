/*
 * Copyright (c) 2016, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
