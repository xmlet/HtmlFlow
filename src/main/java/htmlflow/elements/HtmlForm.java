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
import htmlflow.TextNode;
import htmlflow.attribute.AttributeType;

import java.io.PrintStream;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlForm<T> extends HtmlWriterComposite<T, HtmlForm<T>>{
    public HtmlForm<T> text(String msg){addChild(new TextNode<T>(msg));return this;}
    public HtmlForm<T> br(){addChild(new HtmlBr());return this;}
    public HtmlForm<T> select(String name, String...options){addChild(new HtmlFormSelect(name, options));return this;}
    public HtmlForm<T> inputText(String name){addChild(new HtmlFormInputText(name));return this;}
    public HtmlForm<T> inputText(String name, String id){addChild(new HtmlFormInputText(name, id));return this;}
    public HtmlForm<T> inputSubmit(String value){addChild(new HtmlFormInputSubmit(value));return this;}

    final private String action;

    public HtmlForm(String action) {
        this.action = action;
        addAttr(AttributeType.ACTION.toString(), action);
        addAttr(AttributeType.METHOD.toString(), "post");
        addAttr(AttributeType.ENCTYPE.toString(), "application/x-www-form-urlencoded");
    }

    @Override
    public void doWriteBefore(PrintStream out, int depth) {
        out.print(String.format("<form action=\"%s\" method=\"%s\" enctype=\"%s\">",
                action,
                "post",
                "application/x-www-form-urlencoded"));
        tabs(out, depth + 1);
    }

    @Override
    public String getElementName() {
        return "form";
    }
}
