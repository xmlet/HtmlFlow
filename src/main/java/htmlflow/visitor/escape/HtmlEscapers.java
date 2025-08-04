package htmlflow.visitor.escape;

import htmlflow.visitor.escape.core.Escaper;
import htmlflow.visitor.escape.core.Escapers;

/**
 * Utility class for HTML escaping.
 * <p>
 *     This class provides a static method to obtain an {@link Escaper}
 *     for escaping HTML characters.
 *     The returned escaper will convert special HTML characters
 *     to their corresponding HTML entities,
 *     such as converting `&` to `&amp;`, `<` to `&lt;`, `>` to `&gt;`, etc.
 * </p>
 */
public final class HtmlEscapers {

    /**
     * Returns an {@link Escaper} for HTML escaping.
     *
     * @return the {@link #HTML_ESCAPER} object.
     */
    public static Escaper htmlEscaper() {
        return HTML_ESCAPER;
    }

    /**
     * A pre-configured {@link Escaper} for HTML escaping.
     * <p>
     *     This escaper is initialized with common HTML character replacements
     *     such as `&`, `<`, `>`, `"`, and `'`.
     * </p>
     */
    private static final Escaper HTML_ESCAPER =
            Escapers
                    .builder()
                    .addScape('"', "&quot;")
                    .addScape('\'', "&#39;")
                    .addScape('&', "&amp;")
                    .addScape('<', "&lt;")
                    .addScape('>', "&gt;")
                    .build();
}
