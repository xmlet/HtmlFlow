package htmlflow.test;

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
        String html = HtmlWithoutIndentation
            .bodyDivP
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
            .hotBodyDivP
            .setPrintStream(new PrintStream(mem))
            .write();
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
        String html = HtmlWithoutIndentation
            .bodyPre
            .render();
        String expected = Utils
            .loadLines("htmlWithoutIndentationBodyPre.html")
            .collect(joining());
        assertEquals(expected, html);
    }
}
