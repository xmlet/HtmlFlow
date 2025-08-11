/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package htmlflow.visitor.escape.core;

import static java.util.Collections.max;

import java.util.Map;

/**
 * A class that provides an array-based implementation for character escaping.
 *
 * <p>This class is used to create a mapping of characters to their escape sequences, allowing for
 * efficient character replacement during escaping operations.
 *
 * <p>Derived and adapted from Guava's ArrayBasedEscaperMap class: <a
 * href="https://github.com/google/guava/blob/master/guava/src/com/google/common/escape/ArrayBasedEscaperMap.java">guava</a>
 *
 * <p>Modified by Arthur Oliveira on 04-08-2025
 *
 * @author Arthur Oliveira
 * @author The Guava Authors
 */
final class ArrayBasedEscaperMap {

    /** The replacement array used for escaping characters. */
    private final char[][] replacementArray;

    /**
     * An empty replacement array used when no replacements are defined.
     *
     * <p>This array is used to avoid unnecessary allocations when there are no characters to escape.
     */
    private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];

    /** Exception message for null replacement map. */
    private static final String NULL_MAP_EXCEPTION_MESSAGE =
        "Replacement map cannot be null";

    private ArrayBasedEscaperMap(char[][] replacementArray) {
        this.replacementArray = replacementArray;
    }

    /**
     * Creates a replacement array from the given map.
     *
     * @param replacements the map of characters to their replacement strings
     * @return an {@link ArrayBasedEscaperMap} instance containing the replacement array
     * @throws NullPointerException if the replacements map is null
     */
    public static ArrayBasedEscaperMap create(
        Map<Character, String> replacements
    ) {
        return new ArrayBasedEscaperMap(createReplacementArray(replacements));
    }

    /**
     * Returns the replacement array used for escaping characters.
     *
     * <p>This method provides access to the array that contains the character replacements.
     *
     * @return {@link #replacementArray}
     */
    char[][] getReplacementArray() {
        return replacementArray;
    }

    /**
     * Creates a replacement array from the given map.
     *
     * <p>This method constructs a character array where each index corresponds to a character, and
     * the value at that index is the character's replacement as a character array.
     *
     * @param map the map of characters to their replacement strings
     * @return a character array where each index corresponds to a character, and the value at that
     *     index is the character's replacement as a character array
     * @throws NullPointerException if the map is null
     */
    private static char[][] createReplacementArray(Map<Character, String> map) {
        if (map == null) {
            throw new NullPointerException(NULL_MAP_EXCEPTION_MESSAGE);
        }
        if (map.isEmpty()) {
            return EMPTY_REPLACEMENT_ARRAY;
        }
        char max = max(map.keySet());
        char[][] replacement = new char[max + 1][];
        for (Character c : map.keySet()) {
            replacement[c] = map.get(c).toCharArray();
        }
        return replacement;
    }
}
