package htmlflow.visitor;

import htmlflow.exceptions.HtmlFlowAppendException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class TagsTest {

    @Mock
    private Appendable appendable;

    @Test
    void given_exception_on_append_on_begin_tag_should_rethrow_htmlFlow_exception() throws IOException {

        when(appendable.append(anyChar())).thenThrow(new IOException("oops"));

        assertThrowsExactly(HtmlFlowAppendException.class, () -> Tags.beginTag(appendable, "elem"));
    }

    @Test
    void given_exception_on_append_on_end_tag_should_rethrow_htmlFlow_exception() throws IOException {

        when(appendable.append(anyString())).thenThrow(new IOException("oops"));

        assertThrowsExactly(HtmlFlowAppendException.class, () -> Tags.endTag(appendable, "elem"));
    }

    @Test
    void given_exception_on_append_on_add_attribute_should_rethrow_htmlFlow_exception() throws IOException {

        when(appendable.append(anyChar())).thenThrow(new IOException("oops"));

        assertThrowsExactly(HtmlFlowAppendException.class, () -> Tags.addAttribute(appendable, "elem", "value"));
    }

    @Test
    void given_exception_on_append_on_add_comment_should_rethrow_htmlFlow_exception() throws IOException {

        when(appendable.append(anyString())).thenThrow(new IOException("oops"));

        assertThrowsExactly(HtmlFlowAppendException.class, () -> Tags.addComment(appendable, "elem"));
    }
}
