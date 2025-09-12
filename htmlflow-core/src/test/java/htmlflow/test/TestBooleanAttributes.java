package htmlflow.test;

import htmlflow.HtmlFlow;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBooleanAttributes {
    static final String EXPECTED_NO_ATTRIBUTE = "" +
            "<!DOCTYPE html>" +
            "<html>" +
            "<body>" +
            "<button>" +
            "Foo" +
            "</button>" +
            "</body>" +
            "</html>";

    @Test
    public void view_with_boolean_attribute_too_false_should_suppress_emit(){
        StringBuilder buffer = new StringBuilder();
        HtmlFlow
                .doc(buffer)
                .setIndented(false)
                .html()
                .body()
                .button()
                    .attrDisabled(false)
                    .text("Foo")
                .__() // button
                .__() // body
                .__(); // html
        assertEquals(EXPECTED_NO_ATTRIBUTE, buffer.toString());
    }

    static final String EXPECTED_ATTRIBUTE = "" +
            "<!DOCTYPE html>" +
            "<html>" +
            "<body>" +
            "<button disabled=\"true\">" +
            "Foo" +
            "</button>" +
            "</body>" +
            "</html>";

    @Test
    public void view_with_boolean_attribute_too_true_should_suppress_should_emit(){
        StringBuilder buffer = new StringBuilder();
        HtmlFlow
                .doc(buffer)
                .setIndented(false)
                .html()
                .body()
                .button()
                .attrDisabled(true)
                .text("Foo")
                .__() // button
                .__() // body
                .__(); // html
        assertEquals(EXPECTED_ATTRIBUTE, buffer.toString());
    }

}
