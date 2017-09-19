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

import htmlflow.elements.ElementType;
import htmlflow.elements.HtmlBody;
import htmlflow.elements.HtmlHead;

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
 * The root container for HTML elements, representing the main structure of
 * an  HTML document.
 *
 * @author Miguel Gamboa
 *         created on 29-03-2012
 */
public class HtmlView<T> extends HtmlWriterComposite<T, HtmlView<T>>{

    private static final String header;
    private static final String NEWLINE = System.getProperty("line.separator");

    static {
        try {
            URL headerUrl = HtmlView.class
                    .getClassLoader()
                    .getResource("templates/HtmlView-Header.txt");
            if(headerUrl == null)
                throw new FileNotFoundException("templates/HtmlView-Header.txt");
            InputStream headerStream = headerUrl.openStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(headerStream))) {
                header = reader.lines().collect(Collectors.joining(NEWLINE));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public HtmlHead<T> head(){return addChild(new HtmlHead<T>());}
    public HtmlBody<T> body(){return addChild(new HtmlBody<T>());}

    @Override
    public void doWriteBefore(PrintStream out, int depth) {
        out.println(header);
        super.doWriteBefore(out, depth);
    }

    @Override
    public String getElementName() {
        return ElementType.HTML.toString();
    }
}
