package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import org.junit.Test;
import org.xmlet.htmlapifaster.TextGroup;

import static org.junit.Assert.assertEquals;

public class TestEscapeText {
    /**
     * Related with Issue https://github.com/xmlet/HtmlFlow/issues/55
     */
    @Test
    public void testEscapeScriptEmbedInText() {
        final String expected = "<!DOCTYPE html><html>" +
                "<body>" +
                "&lt;script&gt;alert(&#39;1&#39;)&lt;/script&gt;" +
                "</body>" +
                "</html>";
        HtmlView<String> view = HtmlFlow.<String>view(page -> page
                        .html()
                            .body()
                                .<String>dynamic(TextGroup::text)
                            .__()
                        .__()
                )
                .setIndented(false);
        String html = view.render("<script>alert('1')</script>");
        assertEquals(expected, html);
    }
}
