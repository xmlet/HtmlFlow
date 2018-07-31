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

package htmlflow;

import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Head;
import org.xmlet.htmlapi.Html;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlView<T> implements HtmlWriter<T>{

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
    private ByteArrayOutputStream mem;
    private Html root = new Html();

    public Head<Html> head(){
        return root.head();
    }

    public Body<Html> body(){
        return root.body();
    }

    @Override
    public void write() {
        initByteArrayStream();
        out.print(HEADER);
        root.accept(new HtmlVisitor(out));
        closeByteArrayStream();
    }

    @Override
    public final void write(int depth, T model) {
        initByteArrayStream();
        out.print(HEADER);
        root.accept(new HtmlVisitorBinder<>(out, model));
        closeByteArrayStream();
    }

    private void initByteArrayStream() {
        if(out == null) {
            mem = new ByteArrayOutputStream();
            out = new PrintStream(mem);
        }
    }

    private void closeByteArrayStream() {
        if(mem != null) {
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
    public String toString() {
        return mem != null ? mem.toString(): super.toString();
    }

    public byte[] toByteArray() {
        if(mem == null)
            throw new IllegalStateException("There is not internal stream!");
        return mem.toByteArray();
    }
}
