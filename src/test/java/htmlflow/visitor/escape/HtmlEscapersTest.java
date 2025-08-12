package htmlflow.visitor.escape;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlEscapersTest {

    @Test
    void should_scape_html_characters() {
        String input = "Hello & <world> \"test\" 'example'";
        String expected = "Hello &amp; &lt;world&gt; &quot;test&quot; &#39;example&#39;";

        String escaped = HtmlEscapers.htmlEscaper().escape(input);

        assertEquals(expected, escaped);
    }

    @Test
    void when_no_html_characters_then_return_same_string() {
        String input = "Hello world!";
        String expected = "Hello world!";

        String escaped = HtmlEscapers.htmlEscaper().escape(input);

        assertEquals(expected, escaped);
    }
}