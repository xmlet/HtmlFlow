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

import java.util.Map;

/**
 * An abstract class for escaping characters based on a replacement map.
 * It uses an array to store replacements for characters and provides methods
 * to escape characters and strings.
 *
 * <p>
 *     Derived and adapted from Guava's ArrayBasedCharEscaper class:
 *     <a href="https://github.com/google/guava/blob/master/guava/src/com/google/common/escape/ArrayBasedCharEscaper.java">guava</a>
 * </p>
 *
 * <p>
 *     Modified by Arthur Oliveira on 04-08-2025
 * </p>
 *
 * @author Arthur Oliveira
 * @author The Guava Authors
 */
abstract class ArrayBasedCharEscaper extends CharEscaper {
    /**
     * The array of character replacements. Each index corresponds to a character,
     * and the value at that index is an array of characters representing the replacement.
     * <p>
     *     e.g., if replacements[65] is not null, it means that the character 'A' (ASCII 65)
     *     has a replacement defined.
     * </p>
     */
    private final char[][] replacements;
    /**
     * The length of the replacement array, which is the maximum character code that can be replaced.
     * <p>
     * Characters with codes greater than or equal to this value are not replaced.
     */
    private final int replacementLength;
    /**
     * The minimum safe character code that does not need escaping.
     * <p>
     * Characters below this value are considered unsafe and will be escaped.
     */
    private final char safeMin;
    /**
     * The maximum safe character code that does not need escaping.
     * <p>
     * Characters above this value are considered unsafe and will be escaped.
     */
    private final char safeMax;
    /**
     * Exception message for null code input.
     */
    public static final String NULL_CODE_EXCEPTION_MESSAGE = "Code cannot be null";


    protected ArrayBasedCharEscaper(Map<Character, String> replacementMap, char safeMin, char safeMax) {
        this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax);
    }

    protected ArrayBasedCharEscaper(ArrayBasedEscaperMap replacements, char safeMin, char safeMax) {
        this.replacements = replacements.getReplacementArray();
        this.replacementLength = replacements.getReplacementArray().length;
        if (safeMax < safeMin) {
            this.safeMax = Character.MAX_VALUE;
            this.safeMin = Character.MIN_VALUE;
        } else  {
            this.safeMax = safeMax;
            this.safeMin = safeMin;
        }
    }

    @Override
    public final String escape(String code) throws NullPointerException {
        if (code == null) {
            throw new NullPointerException(NULL_CODE_EXCEPTION_MESSAGE);
        }
        for (int idx = 0; idx < code.length(); idx++) {
            char c = code.charAt(idx);
            if ((c < replacementLength && replacements[c] != null) || c < safeMin || c > safeMax) {
                return escapeSlow(code, idx);
            }
        }
        return code;
    }

    @Override
    public final char[] escape(char c) {
        if (c < replacementLength) {
            char[] chars = replacements[c];
            if (chars != null) {
                return chars;
            }
        }
        if (c >= safeMin && c <= safeMax) {
            return null;
        }
        return escapeUnsafeChar(c);
    }

    /**
     * Escapes a character that is not safe and does not have a replacement.
     * This method should be implemented by subclasses to provide specific escaping logic.
     *
     * @param c the character to escape
     * @return an array of characters representing the escaped version of the input character
     */
    protected abstract char[] escapeUnsafeChar(char c);
}
