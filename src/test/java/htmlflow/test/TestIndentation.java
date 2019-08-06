package htmlflow.test;

import htmlflow.test.views.HtmlWithoutIndentation;
import org.junit.Test;

import static java.util.stream.Collectors.joining;
import static junit.framework.Assert.assertEquals;

public class TestIndentation {

    /**
     * Check behavior reported on Issue:
     * https://github.com/xmlet/HtmlFlow/issues/46
     */
    @Test
    public void testPreElementsWithoutIndentation() {
        String html = HtmlWithoutIndentation
            .bodyDivP
            .render();
        String expected = Utils
            .loadLines("htmlWithoutIndentationBodyDivP.html")
            .collect(joining());
        assertEquals(expected, html);
    }
}
