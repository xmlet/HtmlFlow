package htmlflow.visitor.escape.core;

import java.util.Map;

import static java.util.Collections.max;


final class ArrayBasedEscaperMap {

    private ArrayBasedEscaperMap(char[][] replacementArray) {
        this.replacementArray = replacementArray;
    }

    /**
     * An empty replacement array used when no replacements are defined.
     * <p>
    *  This array is used to avoid unnecessary allocations
    *  when there are no characters to escape.
     */
    private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];

    /**
     * Creates a replacement array from the given map.
     * @param replacements the map of characters to their replacement strings
     * @return an {@link ArrayBasedEscaperMap} instance containing the replacement array
     */
    public static ArrayBasedEscaperMap create(Map<Character, String> replacements) {
        return new ArrayBasedEscaperMap(createReplacementArray(replacements));
    }

    /**
     * The replacement array used for escaping characters.
     */
    private final char[][] replacementArray;

    /**
     * Returns the replacement array used for escaping characters.
     * <p>
     * This method provides access to the array that contains the character replacements.
     *
     * @return {@link #replacementArray}
     */
    char[][] getEmptyReplacementArray() {
        return replacementArray;
    }

    /**
     * Creates a replacement array from the given map.
     * <p>
     * This method constructs a character array where each index corresponds to a character,
     * and the value at that index is the character's replacement as a character array.
     *
     * @param map the map of characters to their replacement strings
     * @return a character array where each index corresponds to a character,
     *         and the value at that index is the character's replacement as a character array
     *
     * @throws NullPointerException if the map is null
     */
    static char[][] createReplacementArray(Map<Character, String> map) {
        if (map == null) {
            throw new NullPointerException("Replacement map cannot be null");
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
