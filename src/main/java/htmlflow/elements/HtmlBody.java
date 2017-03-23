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
import htmlflow.ModelBinder;
import htmlflow.TextNode;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlBody<T> extends HtmlWriterComposite<T, HtmlBody<T>>{

    public HtmlBody<T> heading(int level, String msg){addChild(new HtmlHeading<T>(level)).text(msg); return this;}
    public HtmlBody<T> heading(int level, ModelBinder<T, ?> binder){addChild(new HtmlHeading<T>(level)).text(binder); return this;}
    public HtmlBody<T> text(String msg){addChild(new TextNode<T>(msg));return this;}
    public HtmlBody<T> text(ModelBinder<T, ?> binder){addChild(new TextNode<T>(binder));return this;}
    public HtmlBody<T> br(){addChild(new HtmlBr());return this;}
    public HtmlBody<T> hr(){addChild(new HtmlHr());return this;}
    public HtmlDiv<T> div(){return addChild(new HtmlDiv<T>());}
    public HtmlForm<T> form(String action){return addChild(new HtmlForm<T>(action));}
    public HtmlTable<T> table(){return addChild(new HtmlTable<T>());}
    public HtmlBody<T> p(String msg){addChild(new HtmlP<T>()).text(msg); return this;}
    public HtmlBody<T> p(ModelBinder<T, ?> binder){addChild(new HtmlP<T>()).text(binder); return this;}

    @Override
    public String getElementName() {
        return ElementType.BODY.toString();
    }


}
