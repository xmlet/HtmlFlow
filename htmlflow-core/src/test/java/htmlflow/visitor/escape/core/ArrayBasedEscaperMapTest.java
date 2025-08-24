package htmlflow.visitor.escape.core;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.max;
import static org.junit.jupiter.api.Assertions.*;

class ArrayBasedEscaperMapTest {
    @Test
    void create_call_with_empty_map_should_return_empty_replacement_array() {
        Map<Character, String> emptyMap = Collections.emptyMap();
        ArrayBasedEscaperMap escaperMap = ArrayBasedEscaperMap.create(emptyMap);
        assertEquals(0, escaperMap.getReplacementArray().length);
    }

    @Test
    void create_call_with_non_empty_map_should_return_non_empty_replacement_array() {
        final String alpha = "alpha";
        final String beta = "beta";
        final char alphaChar = 'a';
        final char betaChar = 'b';
        Map<Character, String> replacements = new HashMap<>();
        replacements.put(alphaChar, alpha);
        replacements.put(betaChar, beta);

        ArrayBasedEscaperMap escaperMap = ArrayBasedEscaperMap.create(replacements);

        char[][] replacementArray = escaperMap.getReplacementArray();
        char maxChar = max(replacements.keySet());

        assertEquals(maxChar+1, replacementArray.length);
        assertArrayEquals(alpha.toCharArray(), replacementArray[alphaChar]);
        assertArrayEquals(beta.toCharArray(), replacementArray[betaChar]);
    }

    @Test
    void create_call_with_null_map_should_throw_exception() {
        assertThrows(NullPointerException.class, () -> ArrayBasedEscaperMap.create(null));
    }
}