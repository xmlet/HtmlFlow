package htmlflow.exceptions;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Pedro Fialho
 **/
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HtmlFlowAppendExceptionTest {

    @Test
    void given_underlying_exception_should_append_to_error_message() {
        Exception ex = new RuntimeException("oops");

        HtmlFlowAppendException htmlFlowAppendException = new HtmlFlowAppendException(ex);

        assertEquals("There has been an exception in writing the Html" +
                ", underlying exception is oops", htmlFlowAppendException.getMessage());
    }

    @Test
    void given_message_should_append_to_error_message() {
        HtmlFlowAppendException htmlFlowAppendException = new HtmlFlowAppendException("oops");

        assertEquals("There has been an exception in writing the Html" +
                ", underlying exception is oops", htmlFlowAppendException.getMessage());
    }
}
