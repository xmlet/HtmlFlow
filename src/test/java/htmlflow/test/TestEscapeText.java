package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestEscapeText {
    /**
     * Related with Issue https://github.com/xmlet/HtmlFlow/issues/55
     */
    @Test
    public void testOnEventAttributes() {
        final String expected = "<!DOCTYPE html><html>" +
                "<body>" +
                "<script>alert('1')</script>" +
                "</body>" +
                "</html>";
        HtmlView<String> view = HtmlFlow.<String>view(page -> page
                        .html()
                            .body()
                                .<String>dynamic((el, model) -> el.text(model))
                            .__()
                        .__()
                )
                .setIndented(false);
        String html = view.render("<script>alert('1')</script>");
        assertEquals(expected, html);
    }
}
