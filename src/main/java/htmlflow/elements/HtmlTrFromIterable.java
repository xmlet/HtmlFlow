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

import java.io.PrintStream;

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

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
