package htmlflow.visitor.escape.core;

abstract class CharEscaper extends Escaper{

    /**
     * The multiplier for the size of the escape buffer.
     */
    private static final int ESCAPE_PAD_MULTIPLIER = 2;

    protected CharEscaper() {}

    @Override
    public String escape(String code) {
        if (code == null) {
            throw new NullPointerException("Code cannot be null");
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
     * <p>
     *     This method is called for each character in the input string.
     *     If the character needs to be escaped,
     *     it should return a character array representing the escaped form.
     *     If the character does not need to be escaped, it should return null.
     * @param c the character to escape
     * @return a character array representing the escaped form of the character,
     *         or null if the character does not need to be escaped
     */
    protected abstract char[] escape(char c);

    /**
     * Escapes the input string starting from the specified index.
     * <p>
     * This method is called when at least one character in the input string
     * needs to be escaped. It processes the string from the given index,
     * escaping characters as necessary and returning the fully escaped string.
     *
     * @param code the input string to escape
     * @param idx the index to start escaping from
     * @return the fully escaped string
     */
    protected final String escapeSlow(String code, int idx) {
        int length = code.length();

        char[] escapedChars = Platform.charBufferFromTheadLocal();
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
                escapedSize = sizeNeeded + ESCAPE_PAD_MULTIPLIER * (length - idx);
                escapedChars = growBuffer(escapedChars, escapedIdx, escapedSize);
            }

            if (charsSkipped > 0) {
                code.getChars(lastEscape, idx, escapedChars, escapedIdx);
                escapedIdx += charsSkipped;
            }

            if (escapedLength > 0) {
                System.arraycopy(escapedChar, 0, escapedChars, escapedIdx, escapedLength);
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
     * <p>
     * This method creates a new character array with the specified size,
     * copying the existing characters from the old buffer to the new one.
     *
     * @param buffer the original character buffer
     * @param idx the current index in the buffer
     * @param size the new size for the buffer
     * @return a new character array with the specified size, containing the existing characters
     */
    private static char[] growBuffer(char[] buffer, int idx, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative: " + size);
        }
        char[] newBuffer = new char[size];
        if (idx > 0) {
            System.arraycopy(buffer, 0, newBuffer, 0, idx);
        }
        return newBuffer;
    }
}
