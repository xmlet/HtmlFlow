/*
 * MIT License
 *
 * Copyright (c) 2014-2022, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
 * and Pedro Fialho.
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
package htmlflow;

import htmlflow.visitor.HtmlDocVisitor;
import htmlflow.visitor.HtmlViewVisitor;
import htmlflow.visitor.HtmlViewVisitorAsync;
import htmlflow.visitor.HtmlViewVisitorAsyncHot;
import htmlflow.visitor.HtmlViewVisitorHot;
import htmlflow.visitor.PreprocessingVisitor;
import htmlflow.visitor.PreprocessingVisitorAsync;

/**
 * Factory to create HtmlDoc or HtmlView instances corresponding to static HTMl pages or dynamic pages.
 * HtmlView objects depend on a model.
 */
public class HtmlFlow {
    /**
     * Make private constructor to forbid instantiation.
     */
    private HtmlFlow() {
    }
    /**
     * This will invoke the HtmlTemplate to a PreprocessingVisitor that collects a chain of
     * HtmlContinuation objects containing the static HTML strings and dynamic HTML consumers.
     *
     * @param template   An HtmlTemplate function, which depends on an HtmlView used to create HTMl elements.
     * @param isIndented Set indentation on or off.
     */
    private static PreprocessingVisitor preprocessing(HtmlTemplate template, boolean isIndented) {
        PreprocessingVisitor pre = new PreprocessingVisitor(isIndented);
        HtmlView<?> preView = new HtmlView<>(() -> pre, template, false);
        template.resolve(preView); // ?????? Why ????? This is not the same as next statement ????
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }

    /**
     * @param template   An HtmlTemplate function, which depends on an HtmlView used to create HTMl elements.
     * @param isIndented Set indentation on or off.
     */
    private static PreprocessingVisitorAsync preprocessingAsync(HtmlTemplate template, boolean isIndented) {
        PreprocessingVisitorAsync pre = new PreprocessingVisitorAsync(isIndented);
        HtmlView<?> preView = new HtmlView<>(() -> pre, template, false);
        template.resolve(preView); //  ?????? Why  ????? This is not the same as next statement ????
        /**
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }

    /**
     * Creates a HtmlDoc object corresponding to a static HTML page (without model dependency)
     * that emits HTML to an output Appendable
     *
     * @param out The output Appendable
     */
    public static HtmlDoc doc(Appendable out) {
        return new HtmlDoc(new HtmlDocVisitor(out, true));
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and
     * default indentation on.
     *
     * @param out      Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M>      Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(Appendable out, HtmlTemplate template) {
        return HtmlFlow.view(out, template, true, false, true);
    }

    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and
     * default indentation on.
     *
     * @param out      Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param caching  Enable the use of caching for static HTML blocks. If false, this will create a hot view
     *                 which does not cache static HTML blocks and recalculates HTML on every rendering, useful
     *                 for development scenarios when code is reloaded frequently.
     * @param <M>      Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(Appendable out, boolean caching, HtmlTemplate template) {
        return HtmlFlow.view(out, template, true, false, caching);
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param out        Output PrintStream.
     * @param template   Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param threadSafe Enable the use for thread safety.
     * @param <M>        Type of the model rendered with this view.
     */
    static <M> HtmlView<M> view(Appendable out, HtmlTemplate template, boolean isIndented, boolean threadSafe, boolean caching) {
        if (caching) {
            PreprocessingVisitor pre = preprocessing(template, isIndented);
            return new HtmlView<>(
                    (() -> new HtmlViewVisitor(out, isIndented, pre.getFirst())),
                    template,
                    threadSafe);
        } else {
            return new HtmlViewHot<>(
                    () -> new HtmlViewVisitorHot(out, isIndented),
                    template,
                    threadSafe
            );
        }
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and
     * default indentation on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M>      Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(HtmlTemplate template) {
        return HtmlFlow.view(template, true, false, true);
    }
    /**
     * Creates a Hot HtmlView corresponding to a dynamic HtmlPage with a model and
     * default indentation on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param caching  Enable the use of caching for static HTML blocks. If false, this will create a hot view
     *                 which does not cache static HTML blocks and recalculates HTML on every rendering, useful
     *                 for development scenarios when code is reloaded frequently.
     * @param <M>      Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(boolean caching, HtmlTemplate template) {
        return HtmlFlow.view(template, true, false, caching);
    }
    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param template   Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param threadSafe Enable the use for thread safety.
     * @param <M>        Type of the model rendered with this view.
     */
    static <M> HtmlView<M> view(HtmlTemplate template, boolean isIndented, boolean threadSafe, boolean caching) {
        if (caching) {
            PreprocessingVisitor pre = preprocessing(template, isIndented);
            return new HtmlView<>(
                    (() -> new HtmlViewVisitor(new StringBuilder(), isIndented, pre.getFirst())),
                    template,
                    threadSafe);
        } else {
            return new HtmlViewHot<>(
                    () -> new HtmlViewVisitorHot(new StringBuilder(), isIndented),
                    template,
                    threadSafe
            );
        }
    }
    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model and
     * default indentation on.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M>      Type of the model rendered with this view.
     */
    public static <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template) {
        return HtmlFlow.viewAsync(template, true, false, true);
    }
    /**
     * Creates a Hot HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model and
     * default indentation on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param caching  Enable the use of caching for static HTML blocks. If false, this will create a hot view
     *                 which does not cache static HTML blocks and recalculates HTML on every rendering, useful
     *                 for development scenarios when code is reloaded frequently.
     * @param <M>      Type of the model rendered with this view.
     */
    public static <M> HtmlViewAsync<M> viewAsync(boolean caching, HtmlTemplate template) {
        return HtmlFlow.viewAsync(template, true, false, caching);
    }
    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model.
     *
     * @param template   Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param <M>        Type of the model rendered with this view.
     */
    static <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template, boolean isIndented, boolean threadSafe, boolean caching) {
        if (caching) {
            PreprocessingVisitorAsync pre = preprocessingAsync(template, isIndented);
            return new HtmlViewAsync<>(new HtmlViewVisitorAsync(isIndented, pre.getFirst()), template, threadSafe);
        } else {
            return new HtmlViewAsyncHot<>(new HtmlViewVisitorAsyncHot(isIndented), template, threadSafe);
        }
    }

    /**
     * Creates a Builder to configure HtmlView creation with fluent API.
     * The returned builder can be used to configure settings and build an Engine
     * that creates views with those settings.
     *
     * @return Builder instance for configuring views
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder pattern for creating Engine instances with flexible configuration.
     * Once configured, call build() to get an immutable Engine that creates views.
     */
    public static class Builder {
        private boolean isIndented = true;
        private boolean threadSafe = false;
        private boolean caching = true;

        Builder() {
        }

        /**
         * Configure whether HTML output should be indented.
         *
         * @param indented true for indented output, false for compact output
         * @return this builder instance
         */
        public Builder indented(boolean indented) {
            this.isIndented = indented;
            return this;
        }

        /**
         * Configure thread safety for the view.
         *
         * @param threadSafe true for thread-safe operation, false for single-threaded use
         * @return this builder instance
         */
        public Builder threadSafe(boolean threadSafe) {
            this.threadSafe = threadSafe;
            return this;
        }

        /**
         * Enable or disable caching/preprocessing optimization.
         * When disabled, hot reload mode bypasses preprocessing for dynamic development scenarios.
         *
         * @param caching true for caching/preprocessing, false for hot reload mode
         * @return this builder instance
         */
        public Builder caching(boolean caching) {
            this.caching = caching;
            return this;
        }

        /**
         * Build an immutable Engine with the current configuration.
         * The Engine can be used to create multiple views with the same settings.
         *
         * @return Engine instance configured with current settings
         */
        public Engine build() {
            return new Engine(isIndented, threadSafe, caching);
        }
    }

    /**
     * Immutable Engine for creating HtmlView instances with pre-configured settings.
     * Use the Builder to create Engine instances.
     */
    public static class Engine {
        public final boolean isIndented;
        public final boolean threadSafe;
        public final boolean caching;

        Engine(boolean isIndented, boolean threadSafe, boolean caching) {
            this.isIndented = isIndented;
            this.threadSafe = threadSafe;
            this.caching = caching;
        }

        /**
         * Create an HtmlView instance with the specified template using this Engine's configuration.
         *
         * @param template Function that consumes an HtmlView to produce HTML elements
         * @param <M>      Type of the model rendered with this view
         * @return HtmlView or HtmlViewHot instance based on configuration
         */
        public <M> HtmlView<M> view(HtmlTemplate template) {
            return HtmlFlow.view(template, isIndented, threadSafe, caching);
        }

        /**
         * Create an HtmlView instance with the specified template and output destination
         * using this Engine's configuration.
         *
         * @param out      Appendable output destination
         * @param template Function that consumes an HtmlView to produce HTML elements
         * @param <M>      Type of the model rendered with this view
         * @return HtmlView or HtmlViewHot instance based on configuration
         */
        public <M> HtmlView<M> view(Appendable out, HtmlTemplate template) {
            return HtmlFlow.view(out, template, isIndented, threadSafe, caching);
        }
        /**
         * Create an HtmlViewAsync instance with the specified template using this Engine's configuration.
         *
         * @param template Function that consumes an HtmlView to produce HTML elements
         * @param <M>      Type of the model rendered with this view
         * @return HtmlViewAsync instance based on configuration
         */
        public <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template) {
            return HtmlFlow.viewAsync(template, isIndented, threadSafe, caching);
        }
    }
}
