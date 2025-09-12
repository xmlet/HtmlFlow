/*
 * MIT License
 *
 * Copyright (c) 2025, xmlet HtmlFlow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package htmlflow.visitor;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationSyncStatic;
import java.lang.reflect.Field;
import org.xmlet.htmlapifaster.MfeConfiguration;

/**
 * Specialized preprocessing visitor that supports Micro Frontend (MFE) components.
 *
 * <p>This visitor extends the standard {@link PreprocessingVisitor} with the ability to process
 * and inject MFE-specific script tags into the HTML head section during the preprocessing phase.
 * It's used when MFE support is enabled in the view factory.</p>
 *
 * <p>The class performs the following key operations:</p>
 * <ol>
 *   <li>Collects MFE configurations during template processing through the standard visitor mechanism</li>
 *   <li>Generates necessary script tags for MFE components based on their configurations</li>
 *   <li>Injects these script tags in the head tags of the HTML document</li>
 * </ol>
 *
 * <p>Implementation details:</p>
 * <ul>
 *   <li>Always injects a base module script: {@code <script type="module" src="base.js"></script>}</li>
 *   <li>Adds module scripts for each configured MFE component that provides a script URL</li>
 *   <li>Uses reflection to modify the static HTML blocks in the continuation chain</li>
 *   <li>Targets the first HTML continuation containing the closing head tag</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This visitor requires preEncoding to be enabled as it relies on the
 * preprocessing phase to analyze and modify the HTML structure.</p>
 *
 * @see PreprocessingVisitor
 * @see htmlflow.HtmlMfeConfig
 * @see org.xmlet.htmlapifaster.MfeConfiguration
 */

public class PreprocessingVisitorMfe extends PreprocessingVisitor {

    public static final String HEAD_END_TAG = "</head>";

    public PreprocessingVisitorMfe(boolean isIndented) {
        super(isIndented);
    }

    /**
     * Processes the template and injects MFE script tags.
     *
     * <p>This method first calls the parent's resolve method to create the continuation chain,
     * then traverses the chain to find the head closing tag and injects the necessary script tags
     * for all registered MFE components.</p>
     *
     * @param model the model object (not used in this implementation)
     */
    @Override
    public void resolve(Object model) {
        super.resolve(model);

        final StringBuilder scriptTags = new StringBuilder();
        scriptTags.append("<script type=\"module\" src=\"base.js\"></script>");
        for (MfeConfiguration mfeConfig : this.getMfePage()) {
            final String scriptSrc = mfeConfig.getMfeScriptUrl();
            if (scriptSrc != null && !scriptSrc.isEmpty()) {
                scriptTags
                    .append("<script type=\"module\" src=\"")
                    .append(scriptSrc)
                    .append("\"></script>");
            }
        }

        HtmlContinuation curr = this.first;
        while (curr != null) {
            if (
                curr instanceof HtmlContinuationSyncStatic htmlcontinuationsyncstatic
            ) {
                final String content =
                    htmlcontinuationsyncstatic.staticHtmlBlock;

                if (content.contains(HEAD_END_TAG)) {
                    Field staticHtmlBlockField;
                    try {
                        staticHtmlBlockField =
                            HtmlContinuationSyncStatic.class.getDeclaredField(
                                    "staticHtmlBlock"
                                );
                        staticHtmlBlockField.setAccessible(true);
                        final String newHtml = content.replaceFirst(
                            HEAD_END_TAG,
                            scriptTags + HEAD_END_TAG
                        );
                        staticHtmlBlockField.set(curr, newHtml.intern());
                        break;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            curr = curr.next;
        }
    }
}
