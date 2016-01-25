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

import htmlflow.HtmlWriter;
import htmlflow.ModelBinder;

/**
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
			assert(binder != null);
			out.print(binder.bind(model));
		}
	}
	@Override
	public HtmlWriter<T> setPrintStream(PrintStream out) {
		this.out = out;
		return this;
	}
}
