package htmlflow.visitor.escape.core;

import java.util.HashMap;
import java.util.Map;

public final class Escapers {
    private Escapers() {
    }

    /**
     * Creates a new builder for constructing custom escape sequences.
     * <p>
     * This method provides a fluent interface for defining escape sequences
     * by mapping characters to their replacement strings.
     *
     * @return a new {@link Builder} instance for creating custom escape sequences
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder for creating custom escape sequences.
     */
    public static final class Builder {
        private final Map<Character, String> replacementMap = new HashMap<>();

        private Builder() {}


        /**
         * Assigns a replacement for a character in the escape sequence.
         * <p>
         * If the character is already mapped, it will be replaced with the new value.
         *
         * @param character   the character to escape
         * @param replacement the replacement string for the character
         * @return this builder instance for method chaining
         * @throws NullPointerException if {@code replacement} is null
         */
        public Builder addScape(char character, String replacement) {
            if (replacement == null) {
                throw new NullPointerException("Replacement cannot be null");
            }
            replacementMap.put(character, replacement);
            return this;
        }

        /**
         * TODO: Make Documentation
         */
        public Escaper build() {
            char safeMin = Character.MIN_VALUE;
            char safeMax = Character.MAX_VALUE;
            return new ArrayBasedCharEscaper(replacementMap, safeMin, safeMax) {
                @Override
                protected char[] escapeUnsafe(char c) {
                    return null;
                }
            };
        }

    }

}
