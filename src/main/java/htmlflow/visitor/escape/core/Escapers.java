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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a factory for creating custom escape sequences.
 *
 * <p>This class allows users to define their own escape sequences by mapping characters to specific
 * replacement strings. It is useful for escaping characters in strings that need to be processed or
 * displayed in a specific format.
 *
 * <p>Derived and adapted from Guava's Escapers class: <a
 * href="https://github.com/google/guava/blob/master/guava/src/com/google/common/escape/Escapers.java">guava</a>
 *
 * <p>Modified by Arthur Oliveira on 04-08-2025
 *
 * @author Arthur Oliveira
 * @author The Guava Authors
 */
public final class Escapers {

    private Escapers() {}

    /**
     * Creates a new builder for constructing custom escape sequences.
     *
     * <p>This method provides a fluent interface for defining escape sequences by mapping characters
     * to their replacement strings.
     *
     * @return a new {@link Builder} instance for creating custom escape sequences
     */
    public static Builder builder() {
        return new Builder();
    }

    /** A builder for creating custom escape sequences. */
    public static final class Builder {

        /** Exception message for null replacement input. */
        public static final String NULL_REPLACEMENT_EXCEPTION_MESSAGE =
            "Replacement cannot be null";

        /**
         * A map that holds the character to replacement string mappings.
         *
         * <p>The key is the character to escape, and the value is the string that will replace it in
         * the escaped output.
         */
        private final Map<Character, String> replacementMap = new HashMap<>();

        private Builder() {}

        /**
         * Assigns a replacement for a character in the escape sequence.
         *
         * <p>If the character is already mapped, it will be replaced with the new value.
         *
         * @param character the character to escape
         * @param replacement the replacement string for the character
         * @return this builder instance for method chaining
         * @throws NullPointerException if {@code replacement} is null
         */
        public Builder addScape(char character, String replacement) {
            if (replacement == null) {
                throw new NullPointerException(
                    NULL_REPLACEMENT_EXCEPTION_MESSAGE
                );
            }
            replacementMap.put(character, replacement);
            return this;
        }

        /**
         * Builds the Escaper instance based on the defined replacements.
         *
         * @return a new {@link Escaper} instance configured with the defined replacements
         */
        public Escaper build() {
            char safeMin = Character.MIN_VALUE;
            char safeMax = Character.MAX_VALUE;
            return new ArrayBasedCharEscaper(replacementMap, safeMin, safeMax) {
                @Override
                protected char[] escapeUnsafeChar(char c) {
                    return null;
                }
            };
        }
    }
}
