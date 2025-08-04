package htmlflow.visitor.escape.core;

public abstract class Escaper {
    protected Escaper() {}
    /**
     * Escapes the given text to make it safe for HTML output.
     * <p>
     * Note that this method may treat input characters differently
     * depending on the specific escaper implementation.
     *
     * @param code the literal text to escape
     * @return the escaped text form of {@code text}
     * @throws NullPointerException if {@code text} is null
     * @throws IllegalArgumentException if {@code text} contains badly formed UTF-16 or cannot be escaped for any
     * other reason
     */
    public abstract String escape(String code);
}
