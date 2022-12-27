/*
 * MIT License
 *
 * Copyright (c) 2014-21, Miguel Gamboa (htmlflow.org)
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

import htmlflow.HtmlFlow;
import htmlflow.test.views.HtmlWithoutIndentation;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

public class TestIndentation {

    /**
     * Check behavior reported on Issue:
     * https://github.com/xmlet/HtmlFlow/issues/46
     */
    @Test
    public void testBodyDivPElementsWithoutIndentation() {
        StringBuilder output = new StringBuilder();
        HtmlWithoutIndentation
                .bodyDivP(output);
        String html = output.toString();
        String expected = Utils
            .loadLines("htmlWithoutIndentationBodyDivP.html")
            .collect(joining());
        assertEquals(expected, html);
    }

    /**
     * Check behavior reported on Issue:
     * https://github.com/xmlet/HtmlFlow/issues/46
     */
    @Test
    public void testBodyDivPElementsWithoutIndentationForView() {
        String html = HtmlFlow
                .view(HtmlWithoutIndentation::bodyDivPtemplate)
                .setIndented(false)
                .render();
        String expected = Utils
            .loadLines("htmlWithoutIndentationBodyDivP.html")
            .collect(joining());
        assertEquals(expected, html);
    }

    @Test
    public void testBodyDivPElementsWithoutIndentationOnPrintStream() {
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        HtmlWithoutIndentation
            .hotBodyDivP(new PrintStream(mem));
        String expected = Utils
            .loadLines("htmlWithoutIndentationBodyDivP.html")
            .collect(joining());
        assertEquals(expected, mem.toString());
    }

    /**
     * Check behavior reported on Issue:
     * https://github.com/xmlet/HtmlFlow/issues/46
     */
    @Test
    public void testBodyPreElementsWithoutIndentation() {
        StringBuilder output = new StringBuilder();
        HtmlWithoutIndentation
                .bodyPre(output);
        String html = output.toString();
        String expected = Utils
            .loadLines("htmlWithoutIndentationBodyPre.html")
            .collect(joining());
        assertEquals(expected, html);
    }
}
