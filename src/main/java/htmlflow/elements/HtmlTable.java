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

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlTable<T> extends HtmlWriterComposite<T, HtmlTable<T>>{

    public HtmlTr<T> tr(){return addChild(new HtmlTr<T>());}

    public <S, I extends Iterable<S>> HtmlTable<T> trFromIterable(ModelBinder<S, ?>...binders){
        addChild(new HtmlTrFromIterable<S, I>(binders));
        return this;
    }

    @Override
    public String getElementName() {
      return ElementType.TABLE.toString();
    }
}
