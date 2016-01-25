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

import htmlflow.elements.ElementType;
import htmlflow.elements.HtmlBody;
import htmlflow.elements.HtmlHead;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlView<T> extends HtmlWriterComposite<T, HtmlView<T>>{
	
	public HtmlHead<T> head(){return addChild(new HtmlHead<T>());}
	public HtmlBody<T> body(){return addChild(new HtmlBody<T>());}

	@Override
	public void doWriteBefore(PrintStream out, int depth) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.print("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
	}
	
    @Override
    public String getElementName() {
      return ElementType.HTML.toString();
    }
}
