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

import htmlflow.HtmlWriterComposite;
import htmlflow.ModelBinder;
import htmlflow.TextNode;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public abstract class HtmlTextElement<T> extends HtmlWriterComposite<T, HtmlTextElement>{

	public final void text(String msg){addChild(new TextNode<T>(msg)); }
	public final void text(ModelBinder<T, ?> binder){addChild(new TextNode<T>(binder));}

	protected final String element;
	
	public HtmlTextElement() {
	  element = "";
	}
	
	public HtmlTextElement(String element) {
		this.element = element;
	}

    @Override
    public String getElementName(){
        return  element;
    }

}
