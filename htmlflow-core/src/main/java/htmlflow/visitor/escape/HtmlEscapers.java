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

package htmlflow.visitor.escape;

import htmlflow.visitor.escape.core.Escaper;
import htmlflow.visitor.escape.core.Escapers;

/**
 * Utility class for HTML escaping.
 *
 * <p>This class provides a static method to obtain an {@link Escaper} for escaping HTML characters.
 * The returned escaper will convert special HTML characters to their corresponding HTML entities,
 * such as converting `&` to `&amp;amp;`, `<` to `&amp;lt`, `>` to `&amp;gt`, etc.
 *
 * <p>Derived and adapted from Guava's HtmlEscapers class: <a
 * href="https://github.com/google/guava/blob/master/guava/src/com/google/common/html/HtmlEscapers.java">guava</a>
 *
 * <p>Modified by Arthur Oliveira on 04-08-2025
 *
 * @author Arthur Oliveira
 * @author The Guava Authors
 */
public final class HtmlEscapers {

    private HtmlEscapers() {}

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
     *
     * <p>This escaper is initialized with common HTML character replacements such as `&`, `<`, `>`,
     * `"`, and `'`.
     */
    private static final Escaper HTML_ESCAPER = Escapers
        .builder()
        .addScape('"', "&quot;")
        .addScape('\'', "&#39;")
        .addScape('&', "&amp;")
        .addScape('<', "&lt;")
        .addScape('>', "&gt;")
        .build();
}
