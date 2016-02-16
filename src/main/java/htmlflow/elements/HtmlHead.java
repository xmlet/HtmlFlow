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

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlHead<T> extends HtmlWriterComposite<T, HtmlHead<T>>{

    public HtmlHead<T> title(String msg){addChild(new HtmlTitle<T>()).text(msg);return this;}
    public HtmlHead<T> scriptLink(String src){addChild(new HtmlScriptLink<T>(src));return this;}
    public HtmlScriptBlock<T> scriptBlock(){return addChild(new HtmlScriptBlock<T>());}
    public HtmlHead<T> linkCss(String href){addChild(new HtmlLinkCss<T>(href));return this;}

    @Override
    public String getElementName() {
      return ElementType.HEAD.toString();
    }
}
