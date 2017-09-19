/*
 * MIT License
 *
 * Copyright (c) 2014-16, Miguel Gamboa (gamboa.pt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
        tabs(out, depth);
        out.print(String.format("<form action=\"%s\" method=\"%s\" enctype=\"%s\">",
                action,
                "post",
                "application/x-www-form-urlencoded"));
    }

    @Override
    public String getElementName() {
        return "form";
    }
}
