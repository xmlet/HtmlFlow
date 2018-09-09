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

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Html;

import java.io.*;
import java.net.URL;
import java.util.function.BiConsumer;
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
public class HtmlViewCached<L extends Element> {

    static final String HEADER;
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String HEADER_TEMPLATE = "templates/HtmlView-Header.txt";

    static {
        try {
            URL headerUrl = HtmlViewCached.class
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
    private L root;
    private HtmlVisitorStringBuilderCached visitor;
    private BiConsumer<HtmlViewCached<L>, Object> viewFunction;

    public static HtmlViewCached<Html<Element>> html(BiConsumer<HtmlViewCached<Html<Element>>, Object> viewFunction){
        return new HtmlViewCached<>(viewFunction, new Html<>(new HtmlVisitorStringBuilderCached(new StringBuilder().append(HEADER))));
    }

    public HtmlViewCached(BiConsumer<HtmlViewCached<L>, Object> viewFunction, L root) {
        this.visitor = (HtmlVisitorStringBuilderCached) root.getVisitor();
        this.sb = this.visitor.getStringBuilder();
        this.viewFunction = viewFunction;
        this.root = root;
    }

    public <R> String render(R model) {
        viewFunction.accept(this, model);

        return visitor.setTemplateDefined();
    }

    @SuppressWarnings("unused")
    public HtmlVisitorStringBuilderCached getVisitor() {
        return visitor;
    }

    public L getRoot() {
        return root;
    }

    @SuppressWarnings("unused")
    public L addHeader(){
        sb.append(HEADER);
        return root;
    }

    @SuppressWarnings("unused")
    public <K extends Element, R> void addInnerView(HtmlViewCached<K> innerView, R model){
        visitor.add(innerView.render(model));
    }
}
