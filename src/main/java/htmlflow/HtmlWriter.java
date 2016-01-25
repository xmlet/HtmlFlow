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

package htmlflow;

import java.io.PrintStream;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public interface HtmlWriter<T>{
	/**
	 * Writes into an internal PrintStream the HTML content
	 * of this element with zero indentation and without a
	 * domain object.
	 */
	default void write() {
		write(0, null);
	}
	/**
	 * Writes into an internal PrintStream the HTML content
	 * of this element with initial indentation of zero.
	 *
	 * @param model An optional object model that could be bind to this element.
	 */
	default void write(T model) {
		write(0, model);
	}
	/**
	 * Writes into an internal PrintStream the HTML content
	 * of this element.
	 * 
	 * @param depth The number of tabs indentation.
	 * @param model An optional object model that could be bind to this element. 
	 */
	void write(int depth, T model);
	
	/**
	 * Sets the current PrintStream.
	 * @param out
	 */
	HtmlWriter<T> setPrintStream(PrintStream out);
}
