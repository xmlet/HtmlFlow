package htmlflow.visitor.escape.core;

import java.util.Map;

abstract class ArrayBasedCharEscaper extends CharEscaper {
    private final char[][] replacements;
    private final int replacementLength;
    private final char safeMin;
    private final char safeMax;


    protected ArrayBasedCharEscaper(Map<Character, String> replacementMap, char safeMin, char safeMax) {
        this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax);
    }

    protected ArrayBasedCharEscaper(ArrayBasedEscaperMap replacements, char safeMin, char safeMax) {
        this.replacements = replacements.getEmptyReplacementArray();
        this.replacementLength = replacements.getEmptyReplacementArray().length;
        if (safeMax < safeMin) {
            safeMax = Character.MAX_VALUE;
            safeMin = Character.MIN_VALUE;
        }
        this.safeMin = safeMin;
        this.safeMax = safeMax;
    }

    @Override
    public final String escape(String code) {
        if (code == null) {
            throw new NullPointerException("Code cannot be null");
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
        if (c < safeMin || c > safeMax) {
            return null;
        }
        return escapeUnsafe(c);
    }

    /**
     * TODO: Make Documentation
     */
    protected abstract char[] escapeUnsafe(char c);
}
