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

import org.xmlet.htmlapifaster.AbstractElement;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Head;
import org.xmlet.htmlapifaster.Html;

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
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlView extends AbstractElement<HtmlView, Element> {

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

    private StringBuilder sb;
    private Html<HtmlView> root;

    public static HtmlView html() {
        return html(new StringBuilder());
    }

    public static HtmlView html(StringBuilder sb){
        sb.append(HEADER);
        return new HtmlView(sb);
    }

    public static HtmlView html(PrintStream out){
        out.print(HEADER);
        return new HtmlView(out);
    }

    public HtmlView(StringBuilder sb) {
        super(new HtmlVisitorStringBuilder(sb), "HtmlView", 0);
        this.sb = sb;
        root = new Html<>(this);
    }

    public HtmlView(PrintStream out) {
        super(new HtmlVisitorPrintStream(out), "HtmlView", 0);
        root = new Html<>(this);
    }

    public Head<Html<HtmlView>> head(){
        return root.head();
    }

    public Body<Html<HtmlView>> body(){
        return root.body();
    }

    public String render() {
        if(sb == null)
            throw new IllegalStateException("Missing StringBuilder!!!! HtmlView not initialized with a StringBuilder!");
        return sb.toString();
    }

    @Override
    public HtmlView self() {
        return this;
    }

}
