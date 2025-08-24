package htmlflow.visitor.escape.core;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ArrayBasedCharEscaperTest {

    @Test
    void when_escape_code_is_null_then_throw_NullPointerException() {
        Map<Character, String> replacementMap = Collections.emptyMap();
        ArrayBasedCharEscaper escaper = new ArrayBasedCharEscaper(replacementMap, Character.MIN_VALUE, Character.MAX_VALUE) {
            @Override
            protected char[] escapeUnsafeChar(char c) {
                return null; // No specific escaping logic needed for this test
            }
        };

        NullPointerException ex = assertThrows(NullPointerException.class, () -> escaper.escape(null));
        assertEquals(ArrayBasedCharEscaper.NULL_CODE_EXCEPTION_MESSAGE, ex.getMessage());
    }

    @Test
    void when_escape_code_is_empty_then_return_empty_string() {
        Map<Character, String> replacementMap = Collections.emptyMap();
        ArrayBasedCharEscaper escaper = new ArrayBasedCharEscaper(replacementMap, Character.MIN_VALUE, Character.MAX_VALUE) {
            @Override
            protected char[] escapeUnsafeChar(char c) {
                return null; // No specific escaping logic needed for this test
            }
        };

        String result = escaper.escape("");
        assertEquals("", result);
    }

    @Test
    void when_escape_triggers_escapeUnsafeChar_then_it_is_called() {
        final char safeMin = (char) 10;
        final char safeMax = (char) 20;
        final char unsafe = (char) 25;
        Map<Character, String> replacementMap = Collections.emptyMap();
        ArrayBasedCharEscaper escaper = spy(new ArrayBasedCharEscaper(replacementMap, safeMin, safeMax) {
            @Override
            protected char[] escapeUnsafeChar(char c) {
                return new char[]{'x'};
            }
        });

        escaper.escape(unsafe);

        verify(escaper).escapeUnsafeChar(unsafe);

        final char[] expected = new char[]{'x'};
        final char[] result = escaper.escape(unsafe);
        assertArrayEquals(expected, result);
    }

    @Test
    void should_escape_correctly_for_safe_characters() {
        final char safeMin = 'A';
        final char safeMax = 'C';
        final String escapeExample = "safeChar";
        Map<Character, String> replacementMap = new HashMap<>();
        replacementMap.put('B', escapeExample);

        ArrayBasedCharEscaper escaper = new ArrayBasedCharEscaper(replacementMap, safeMin, safeMax) {
            @Override
            protected char[] escapeUnsafeChar(char c) {
                return null; // No specific escaping logic needed for this test
            }
        };

        String input = "A_B_C_D";
        String result = escaper.escape(input);
        assertEquals("A_" + escapeExample + "_C_D", result);
    }
}