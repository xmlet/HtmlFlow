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
package htmlflow.test;

import htmlflow.HtmlView;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Miguel Gamboa
 * Created on 22-01-2016.
 */
public class Utils {

    static final Pattern NEWLINE = Pattern.compile("\\n");

    private Utils() {}

    static Element getRootElement(byte[] input) {
        W3CDom w3cDom = new W3CDom();
        Document doc = w3cDom.fromJsoup(Jsoup.parse(new String(input, StandardCharsets.UTF_8)));
        return doc.getDocumentElement();
    }

    static <T> Stream<String> htmlWrite(ByteArrayOutputStream mem){
        InputStreamReader actual = new InputStreamReader(new ByteArrayInputStream(mem.toByteArray()));
        return new BufferedReader(actual).lines();
    }

    static <T> Stream<String> htmlRender(HtmlView view, T model){
        String html = view.render(model);
        return NEWLINE.splitAsStream(html);
    }

    public static Stream<String> loadLines(String path) {
        try{
            InputStream in = TestDivDetails.class
                    .getClassLoader()
                    .getResource(path)
                    .openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().onClose(asUncheckedRunnable(reader));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Convert a Closeable to a Runnable by converting checked IOException
     * to UncheckedIOException
     */
    private static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

}
