package htmlflow.visitor.escape.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CharEscaperTest {
    static class TestableCharEscaper extends CharEscaper {
        @Override
        protected char[] escape(char c) {
            return c == 'a' ? new char[]{'[', 'a', ']'} : null;
        }

        public String callEscapeSlow(String code, int idx) {
            return escapeSlow(code, idx);
        }
    }

    @Test
    void escape_should_throw_NullPointerException_when_code_is_null() {
        CharEscaper escaper = new CharEscaper() {
            @Override
            protected char[] escape(char c) {
                return null; // No escaping logic needed for this test
            }
        };

        NullPointerException ex = assertThrows(NullPointerException.class, () -> escaper.escape(null));
        Assertions.assertEquals(CharEscaper.NULL_CODE_EXCEPTION_MESSAGE, ex.getMessage());
    }

    @Test
    void escape_should_return_original_string_when_no_characters_to_escape() {
        CharEscaper escaper = new CharEscaper() {
            @Override
            protected char[] escape(char c) {
                return null; // No escaping logic needed for this test
            }
        };

        String input = "Hello, World!";
        String result = escaper.escape(input);
        Assertions.assertEquals(input, result);
    }

    @Test
    void escape_char_should_be_called_for_each_character() {
        CharEscaper escaper = spy(new CharEscaper() {
            @Override
            protected char[] escape(char c) {
                return null; // No escaping logic needed for this test
            }
        });
        String input = "String";
        escaper.escape(input);
        for (char c : input.toCharArray()) {
            verify(escaper).escape(c);
        }
    }

    @Test
    void escapeSlow_should_escape_characters_from_given_index() {
        TestableCharEscaper escaper = new TestableCharEscaper();
        String input = "baac";
        String result = escaper.callEscapeSlow(input, 1);
        Assertions.assertEquals("b[a][a]c", result);
    }
}