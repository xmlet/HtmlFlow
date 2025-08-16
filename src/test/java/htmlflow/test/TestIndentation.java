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
import htmlflow.HtmlView;
import htmlflow.HtmlViewAsync;
import htmlflow.test.views.HtmlWithoutIndentation;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

public class TestIndentation {

    static final String EXPECTED = "<div><textarea>Sample text\n" +
            "foo" + lineSeparator() +
            "bar</textarea><script>// some comment\n" +
            "console.log('Hello world');</script></div>";

    @Test
    public void docWithNoIndent() {
        StringBuilder sb = new StringBuilder();
        HtmlFlow.doc(sb).setIndented(false)
                .div()
                    .textarea()
                        .text("Sample text\n" +
                              "foo" + lineSeparator() +
                              "bar")
                    .__()
                    .script()
                    .raw("// some comment\n" +
                        "console.log('Hello world');")
                .__() // script
                .__(); // div

        String actual = sb.toString();
        assertEquals(EXPECTED, actual);
    }

    @Test
    public void viewWithNoIndent() {
        HtmlView<?> view = HtmlFlow.view(page -> page
                .div()
                .textarea()
                .text("Sample text\n" +
                        "foo" + lineSeparator() +
                        "bar")
                .__()
                .script()
                .raw("// some comment\n" +
                        "console.log('Hello world');")
                .__() // script
                .__()); // div

        String actual = view.setIndented(false).render();
        assertEquals(EXPECTED, actual);
    }

    @Test
    public void viewWithTextAreaAndIndent() {
        HtmlView<?> view = HtmlFlow.view(page -> page
                .div()
                .textarea()
                .text("Sample text\nfoo\nbar")
                .__()
                .script()
                .raw("// some comment" + lineSeparator() +
                        "console.log('Hello world');")
                .__() // script
                .__()); // div


        String expectedResult = 
        "<div>" + lineSeparator() +
        "\t<textarea>"+lineSeparator() +
        "Sample text" + lineSeparator() +
        "foo" + lineSeparator() +
        "bar</textarea>" + lineSeparator() +
        "\t<script>" + lineSeparator() +
        "\t\t// some comment" + lineSeparator() +
        "console.log('Hello world');" + lineSeparator() +
        "\t</script>" + lineSeparator() +
        "</div>";

        String actual = view.setIndented(true).render();
        assertEquals(expectedResult, actual);
    }
    @Test
    public void viewAsyncWithNoIndent() {
        HtmlViewAsync<?> view = HtmlFlow.viewAsync(page -> page
                .div()
                .textarea()
                .text("Sample text\n" +
                        "foo" + lineSeparator() +
                        "bar")
                .__()
                .script()
                .raw("// some comment\n" +
                        "console.log('Hello world');")
                .__() // script
                .__()); // div

        String actual = view.setIndented(false).renderAsync().join();
        assertEquals(EXPECTED, actual);
    }

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
