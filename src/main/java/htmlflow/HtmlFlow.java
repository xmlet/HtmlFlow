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

import htmlflow.visitor.*;

/**
 * Factory to create HtmlDoc or HtmlView instances corresponding to static HTMl pages or dynamic
 * pages. HtmlView objects depend on a model.
 */
public class HtmlFlow {

    /** Make private constructor to forbid instantiation. */
    private HtmlFlow() {}

    /**
     * This will invoke the HtmlTemplate to a PreprocessingVisitor that collects a chain of
     * HtmlContinuation objects containing the static HTML strings and dynamic HTML consumers.
     *
     * @param template An HtmlTemplate function, which depends on an HtmlView used to create HTMl
     *     elements.
     * @param isIndented Set indentation on or off.
     */
    private static PreprocessingVisitor preprocessing(
        HtmlTemplate template,
        boolean isIndented
    ) {
        PreprocessingVisitor pre = new PreprocessingVisitor(isIndented);
        HtmlView<?> preView = new HtmlView<>(() -> pre, template, false);
        /*
         * 1st Performs rendering by invoking the function defined as the template, collecting the
         * resulting static HTML into an internal StringBuffer.
         */
        template.resolve(preView);
        /*
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         * 2nd Simply invokes resolve in PreprocessingVisitor, which creates only
         * the final HtmlContinuationSyncStatic containing the remaining static HTML
         * accumulated in the internal StringBuffer out.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }

    private static PreprocessingVisitorMfe preprocessingMfe(HtmlTemplate template, boolean isIndented) {
        PreprocessingVisitorMfe processView = new PreprocessingVisitorMfe(isIndented);
        HtmlView<?> preView = new HtmlView<>(() -> processView, template, false);
        // first process
        template.resolve(preView);
        // second process
        preView.getVisitor().resolve(null);
        return processView;
    }

    /**
     * @param template An HtmlTemplate function, which depends on an HtmlView used to create HTMl
     *     elements.
     * @param isIndented Set indentation on or off.
     */
    private static PreprocessingVisitorAsync preprocessingAsync(
        HtmlTemplate template,
        boolean isIndented
    ) {
        PreprocessingVisitorAsync pre = new PreprocessingVisitorAsync(
            isIndented
        );
        HtmlView<?> preView = new HtmlView<>(() -> pre, template, false);
        /*
         * 1st Performs rendering by invoking the function defined as the template, collecting the
         * resulting static HTML into an internal StringBuffer.
         */
        template.resolve(preView);
        /*
         * NO problem with null model. We are just preprocessing static HTML blocks.
         * Thus, dynamic blocks which depend on model are not invoked.
         * 2nd Simply invokes resolve in PreprocessingVisitor, which creates only
         * the final HtmlContinuationSyncStatic containing the remaining static HTML
         * accumulated in the internal StringBuffer out.
         */
        preView.getVisitor().resolve(null);
        return pre;
    }

    /**
     * Creates a HtmlDoc object corresponding to a static HTML page (without model dependency) that
     * emits HTML to an output Appendable
     *
     * @param out The output Appendable
     */
    public static HtmlDoc doc(Appendable out) {
        return new HtmlDoc(new HtmlDocVisitor(out, true));
    }

    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and default indentation on.
     *
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(Appendable out, HtmlTemplate template) {
        return HtmlFlow.view(out, template, true, false, true);
    }

    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and default indentation on.
     *
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param preEncoding Enable the use of preEncoding for static HTML blocks. If false, this will
     *     create a hot view which does not store static HTML blocks and recalculates HTML on every
     *     rendering, useful for development scenarios when code is reloaded frequently.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(
        Appendable out,
        boolean preEncoding,
        HtmlTemplate template
    ) {
        return HtmlFlow.view(out, template, true, false, preEncoding);
    }

    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param out Output PrintStream.
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param threadSafe Enable the use for thread safety.
     * @param <M> Type of the model rendered with this view.
     */
    static <M> HtmlView<M> view(
        Appendable out,
        HtmlTemplate template,
        boolean isIndented,
        boolean threadSafe,
        boolean preEncoding
    ) {
        if (preEncoding) {
            PreprocessingVisitor pre = preprocessing(template, isIndented);
            return new HtmlView<>(
                (() -> new HtmlViewVisitor(out, isIndented, pre.getFirst())),
                template,
                threadSafe
            );
        } else {
            return new HtmlViewHot<>(
                () -> new HtmlViewVisitorHot(out, isIndented),
                template,
                threadSafe
            );
        }
    }

    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model and default indentation on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(HtmlTemplate template) {
        return HtmlFlow.view(template, true, false, true);
    }

    /**
     * Creates a Hot HtmlView corresponding to a dynamic HtmlPage with a model and default indentation
     * on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param preEncoding Enable the use of preEncoding for static HTML blocks. If false, this will
     *     create a hot view which does not store static HTML blocks and recalculates HTML on every
     *     rendering, useful for development scenarios when code is reloaded frequently.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlView<M> view(
        boolean preEncoding,
        HtmlTemplate template
    ) {
        return HtmlFlow.view(template, true, false, preEncoding);
    }

    /**
     * Creates a HtmlView corresponding to a dynamic HtmlPage with a model.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param threadSafe Enable the use for thread safety.
     * @param <M> Type of the model rendered with this view.
     */
    static <M> HtmlView<M> view(
        HtmlTemplate template,
        boolean isIndented,
        boolean threadSafe,
        boolean preEncoding
    ) {
        if (preEncoding) {
            PreprocessingVisitor pre = preprocessing(template, isIndented);
            return new HtmlView<>(
                (
                    () ->
                        new HtmlViewVisitor(
                            new StringBuilder(),
                            isIndented,
                            pre.getFirst()
                        )
                ),
                template,
                threadSafe
            );
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
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template) {
        return HtmlFlow.viewAsync(template, true, false, true);
    }

    /**
     * Creates a Hot HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model and
     * default indentation on.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param preEncoding Enable the use of preEncoding for static HTML blocks. If false, this will
     *     create a hot view which does not store static HTML blocks and recalculates HTML on every
     *     rendering, useful for development scenarios when code is reloaded frequently.
     * @param <M> Type of the model rendered with this view.
     */
    public static <M> HtmlViewAsync<M> viewAsync(
        boolean preEncoding,
        HtmlTemplate template
    ) {
        return HtmlFlow.viewAsync(template, true, false, preEncoding);
    }

    /**
     * Creates a HtmlViewAsync corresponding to a dynamic HtmlPage with an asynchronous model.
     *
     * @param template Function that consumes an HtmlView to produce HTML elements.
     * @param isIndented Set indentation on or off.
     * @param <M> Type of the model rendered with this view.
     */
    static <M> HtmlViewAsync<M> viewAsync(
        HtmlTemplate template,
        boolean isIndented,
        boolean threadSafe,
        boolean preEncoding
    ) {
        if (preEncoding) {
            PreprocessingVisitorAsync pre = preprocessingAsync(
                template,
                isIndented
            );
            return new HtmlViewAsync<>(
                new HtmlViewVisitorAsync(isIndented, pre.getFirst()),
                template,
                threadSafe
            );
        } else {
            return new HtmlViewAsyncHot<>(
                new HtmlViewVisitorAsyncHot(
                    isIndented,
                    () -> preprocessingAsync(template, isIndented)
                ),
                template,
                threadSafe
            );
        }
    }

    /**
     * Immutable Engine for creating HtmlView instances with pre-configured settings. Use the Builder
     * to create Engine instances.
     */
    public static class ViewFactory {

        public final boolean isIndented;
        public final boolean threadSafe;
        public final boolean preEncoding;

        /**
         * Creates a Builder to configure HtmlView creation with fluent API. The returned builder can be
         * used to configure settings and build an Engine that creates views with those settings.
         *
         * @return Builder instance for configuring views
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder pattern for creating Engine instances with flexible configuration. Once configured,
         * call build() to get an immutable Engine that creates views.
         */
        public static class Builder {

            private boolean isIndented = true;
            private boolean threadSafe = false;
            private boolean preEncoding = true;

            Builder() {}

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
             * Enable or disable preEncoding/preprocessing optimization. When disabled, hot reload mode
             * bypasses preprocessing for dynamic development scenarios.
             *
             * @param preEncoding true for preEncoding/preprocessing, false for hot reload mode
             * @return this builder instance
             */
            public Builder preEncoding(boolean preEncoding) {
                this.preEncoding = preEncoding;
                return this;
            }

            /**
             * Build an immutable Engine with the current configuration. The Engine can be used to create
             * multiple views with the same settings.
             *
             * @return Engine instance configured with current settings
             */
            public ViewFactory build() {
                return new ViewFactory(isIndented, threadSafe, preEncoding);
            }
        }

        ViewFactory(
            boolean isIndented,
            boolean threadSafe,
            boolean preEncoding
        ) {
            this.isIndented = isIndented;
            this.threadSafe = threadSafe;
            this.preEncoding = preEncoding;
        }

        /**
         * Create an HtmlView instance with the specified template using this Engine's configuration.
         *
         * @param template Function that consumes an HtmlView to produce HTML elements
         * @param <M> Type of the model rendered with this view
         * @return HtmlView or HtmlViewHot instance based on configuration
         */
        public <M> HtmlView<M> view(HtmlTemplate template) {
            return HtmlFlow.view(template, isIndented, threadSafe, preEncoding);
        }

        /**
         * Create an HtmlView instance with the specified template and output destination using this
         * Engine's configuration.
         *
         * @param out Appendable output destination
         * @param template Function that consumes an HtmlView to produce HTML elements
         * @param <M> Type of the model rendered with this view
         * @return HtmlView or HtmlViewHot instance based on configuration
         */
        public <M> HtmlView<M> view(Appendable out, HtmlTemplate template) {
            return HtmlFlow.view(
                out,
                template,
                isIndented,
                threadSafe,
                preEncoding
            );
        }

        /**
         * Create an HtmlViewAsync instance with the specified template using this Engine's
         * configuration.
         *
         * @param template Function that consumes an HtmlView to produce HTML elements
         * @param <M> Type of the model rendered with this view
         * @return HtmlViewAsync instance based on configuration
         */
        public <M> HtmlViewAsync<M> viewAsync(HtmlTemplate template) {
            return HtmlFlow.viewAsync(
                template,
                isIndented,
                threadSafe,
                preEncoding
            );
        }

        public static <M>HtmlView<M> mfe(HtmlTemplate template) {
            PreprocessingVisitorMfe pre = preprocessingMfe(template, false);
            return new HtmlView<>(
                    (() -> new HtmlViewVisitor(new StringBuilder(), true, pre.getFirst())),
                    template,
                    false);
        }
    }
}
