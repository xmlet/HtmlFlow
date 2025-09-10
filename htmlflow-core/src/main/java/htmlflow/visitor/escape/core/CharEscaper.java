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

/**
 * An abstract class that provides a base implementation for character escaping.
 *
 * <p>This class defines the structure for escaping characters in a string, allowing subclasses to
 * implement specific escaping logic.
 *
 * <p>Derived and adapted from Guava's CharEscaper class: <a
 * href="https://github.com/google/guava/blob/master/guava/src/com/google/common/escape/CharEscaper.java">guava</a>
 *
 * <p>Modified by Arthur Oliveira on 04-08-2025
 *
 * @author Arthur Oliveira
 * @author The Guava Authors
 */
abstract class CharEscaper extends Escaper {

    /**
     * The multiplier for the size of the escape buffer.
     */
    private static final int ESCAPE_PAD_MULTIPLIER = 2;

    /**
     * The size of the buffer used for character escaping.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Exception message for null code input.
     */
    public static final String NULL_CODE_EXCEPTION_MESSAGE =
            "Code cannot be null";

    /**
     * Exception message for negative size in buffer growth.
     */
    public static final String NEGATIVE_SIZE_EXCEPTION_MESSAGE =
            "Size cannot be negative: ";

    protected CharEscaper() {
    }

    @Override
    public String escape(String code) throws NullPointerException {
        if (code == null) {
            throw new NullPointerException(NULL_CODE_EXCEPTION_MESSAGE);
        }
        int length = code.length();
        for (int idx = 0; idx < length; idx++) {
            if (escape(code.charAt(idx)) != null) {
                return escapeSlow(code, idx);
            }
        }
        return code;
    }

    /**
     * Escapes a single character.
     *
     * <p>This method is called for each character in the input string.
     *
     * @param c the character to escape
     * @return a character array representing the escaped form of the character, or null if the
     * character does not need to be escaped
     */
    protected abstract char[] escape(char c);

    /**
     * Escapes the input string starting from the specified index.
     *
     * <p>This method is called when at least one character in the input string needs to be escaped.
     * It processes the string from the given index, escaping characters as necessary and returning
     * the fully escaped string.
     *
     * @param code the input string to escape
     * @param idx  the index to start escaping from
     * @return the fully escaped string
     */
    protected final String escapeSlow(String code, int idx) {
        int length = code.length();

        char[] escapedChars = new char[BUFFER_SIZE];
        int escapedSize = escapedChars.length;
        int escapedIdx = 0;
        int lastEscape = 0;

        for (; idx < length; idx++) {
            char[] escapedChar = escape(code.charAt(idx));

            if (escapedChar == null) {
                continue;
            }

            int escapedLength = escapedChar.length;
            int charsSkipped = idx - lastEscape;

            int sizeNeeded = escapedIdx + charsSkipped + escapedLength;
            if (escapedSize < sizeNeeded) {
                escapedSize =
                        sizeNeeded + ESCAPE_PAD_MULTIPLIER * (length - idx);
                escapedChars =
                        growBuffer(escapedChars, escapedIdx, escapedSize);
            }

            if (charsSkipped > 0) {
                code.getChars(lastEscape, idx, escapedChars, escapedIdx);
                escapedIdx += charsSkipped;
            }

            if (escapedLength > 0) {
                System.arraycopy(
                        escapedChar,
                        0,
                        escapedChars,
                        escapedIdx,
                        escapedLength
                );
                escapedIdx += escapedLength;
            }
            lastEscape = idx + 1;
        }

        int charsLeft = length - lastEscape;
        if (charsLeft > 0) {
            int neededSize = escapedIdx + charsLeft;
            if (escapedSize < neededSize) {
                escapedChars = growBuffer(escapedChars, escapedIdx, neededSize);
            }
            code.getChars(lastEscape, length, escapedChars, escapedIdx);
            escapedIdx = neededSize;
        }
        return new String(escapedChars, 0, escapedIdx);
    }

    /**
     * Grows the buffer to accommodate the new size.
     *
     * <p>This method creates a new character array with the specified size, copying the existing
     * characters from the old buffer to the new one.
     *
     * @param buffer the original character buffer
     * @param idx    the current index in the buffer
     * @param size   the new size for the buffer
     * @return a new character array with the specified size, containing the existing characters
     */
    private static char[] growBuffer(char[] buffer, int idx, int size) {
        if (size < 0) {
            throw new IllegalArgumentException(
                    NEGATIVE_SIZE_EXCEPTION_MESSAGE + size
            );
        }
        char[] newBuffer = new char[size];
        if (idx > 0) {
            System.arraycopy(buffer, 0, newBuffer, 0, idx);
        }
        return newBuffer;
    }
}
