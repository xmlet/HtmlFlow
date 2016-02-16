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
public class HtmlDiv<T> extends HtmlWriterComposite<T, HtmlDiv<T>> {

    public HtmlTable<T> table(){return addChild(new HtmlTable<T>());}
    public HtmlDiv<T> text(String msg){addChild(new TextNode<T>(msg));return this;}
    public HtmlDiv<T> text(ModelBinder<T,?> binder){addChild(new TextNode<T>(binder));return this;}
    public HtmlDiv<T> br(){addChild(new HtmlBr());return this;}
    public HtmlDiv<T> hr(){addChild(new HtmlHr());return this;}
    public HtmlDiv<T> div(){return addChild(new HtmlDiv<T>());}
    public HtmlForm<T> form(String action){return addChild(new HtmlForm<T>(action));}

    @Override
    public String getElementName() {
        return ElementType.DIV.toString();
    }
}
