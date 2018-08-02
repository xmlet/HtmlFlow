/*
 * MIT License
 *
 * Copyright (c) 2014-18, Miguel Gamboa (gamboa.pt)
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

package htmlflow;

import org.xmlet.htmlapi.AbstractElement;
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.ElementVisitor;
import org.xmlet.htmlapi.Head;
import org.xmlet.htmlapi.Html;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * The root container for HTML elements.
 * It it responsible for managing the {@code org.xmlet.htmlapi.ElementVisitor}
 * implementation, which is responsible for printing the tree of elements and
 * attributes.
 *
 * @param <T> The type of domain object bound to this View.
 *
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlView<T> extends AbstractElement<HtmlView<T>, Element> implements HtmlWriter<T>{

    private static final String HEADER;
    private static final String NEWLINE = System.getProperty("line.separator");
    public static final String HEADER_TEMPLATE = "templates/HtmlView-Header.txt";

    static {
        try {
            URL headerUrl = HtmlView.class
                    .getClassLoader()
                    .getResource(HEADER_TEMPLATE);
            if(headerUrl == null)
                throw new FileNotFoundException(HEADER_TEMPLATE);
            InputStream headerStream = headerUrl.openStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(headerStream))) {
                HEADER = reader.lines().collect(Collectors.joining(NEWLINE));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PrintStream out;
    private Html<HtmlView<T>> root;

    /**
     * @param <R> The type of domain object bound to the html.
     */
    public static <R> HtmlView<R> html(){
        return new HtmlView<>();
    }

    public HtmlView() {
        super("HtmlView");
        root = new Html<>(this);
        addChild(root);
    }

    public Head<Html<HtmlView<T>>> head(){
        return root.head();
    }

    public Body<Html<HtmlView<T>>> body(){
        return root.body();
    }

    public String render() {
        StringBuilder sb = new StringBuilder(HEADER);
        root.accept(new HtmlVisitorStringBuilder(sb));
        return sb.toString();
    }

    public String render(T model) {
        StringBuilder sb = new StringBuilder(HEADER);
        root.accept(new HtmlVisitorStringBuilderBinder<>(sb, model));
        return sb.toString();
    }

    @Override
    public void write() {
        out.print(HEADER);
        root.accept(new HtmlVisitor(out));
        closeByteArrayStream();
    }

    @Override
    public final void write(int depth, T model) {
        out.print(HEADER);
        root.accept(new HtmlVisitorBinder<>(out, model));
        closeByteArrayStream();
    }

    private void closeByteArrayStream() {
        if(out != null) {
            out.flush();
            out.close();
            out = null;
        }
    }

    @Override
    public HtmlWriter<T> setPrintStream(PrintStream out) {
        this.out = out;
        return this;
    }


    @Override
    public HtmlView<T> self() {
        return this;
    }

    @Override
    public void accept(ElementVisitor visitor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HtmlView cloneElem() {
        throw new UnsupportedOperationException();
    }
}
