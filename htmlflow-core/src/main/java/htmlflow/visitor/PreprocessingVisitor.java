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

package htmlflow.visitor;

import static htmlflow.visitor.PreprocessingVisitor.HtmlContinuationSetter.setNext;
import static htmlflow.visitor.Tags.ATTRIBUTE_MID;
import static htmlflow.visitor.Tags.QUOTATION;
import static htmlflow.visitor.Tags.SPACE;

import htmlflow.continuations.HtmlContinuation;
import htmlflow.continuations.HtmlContinuationSyncCloseAndIndent;
import htmlflow.continuations.HtmlContinuationSyncDynamic;
import htmlflow.continuations.HtmlContinuationSyncForEach;
import htmlflow.continuations.HtmlContinuationSyncStatic;
import htmlflow.continuations.HtmlContinuationSyncValue;
import htmlflow.continuations.HtmlContinuationSyncValue.Kind;
import htmlflow.continuations.HtmlContinuationSyncWhen;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

/**
 * This visitor is used to make a preprocessing resolution of an HtmlTemplate. It will collect the
 * resulting HTML from visiting static HTML elements into an auxiliary StringBuilder that later is
 * extracted to: String staticHtml = sb.substring(staticBlockIndex); to create an
 * HtmlContinuationSyncStatic object. It also interleaves the creation of HtmlContinuationDynamic
 * nodes that only store dynamicHtmlBlock objects corresponding to a BiConsumer<E, U> (being E an
 * element and U the model). The U comes from external module HtmlApiFaster whose classes are not
 * strongly typed with the Model. Thus, only the dynamic() and visitDynamic() methods in
 * HtmlApiFaster were made generic to carry a type parameter U corresponding to the type of the
 * Model.
 */
public class PreprocessingVisitor extends HtmlVisitor {

    private static final String NOT_SUPPORTED_ERROR =
        "This is a PreprocessingVisitor used to compile templates and not intended to support" +
        " HTML views!";

    /** The internal String builder beginning index of a static HTML block. */
    protected int staticBlockIndex = 0;

    /**
     * True when the leading whitespace of the next static block belongs to the page. A dynamic
     * block re-emits its own indentation on every render, so we trim the block that follows it.
     * A value slot doesn't, and trimming there would delete real whitespace.
     */
    private boolean keepLeadingSpace = false;

    /** The first node to be processed. */
    protected HtmlContinuation first;

    /** The last HtmlContinuation */
    protected HtmlContinuation last;

    public PreprocessingVisitor(boolean isIndented) {
        super(new StringBuilder(), isIndented);
    }

    /** The main StringBuilder. */
    public final StringBuilder sb() {
        return (StringBuilder) out;
    }

    public HtmlContinuation getFirst() {
        return first;
    }

    /**
     * Here we are creating 2 HtmlContinuation objects: one for previous static HTML and a next one
     * corresponding to the consumer passed to dynamic(). We will first create the dynamic
     * continuation that will be the next node of the static continuation.
     *
     * <p>U is the type of the model passed to the dynamic HTML block that is the same as T in this
     * visitor. Yet, since it came from HtmlApiFaster that is not typed by the Model, then we have to
     * use another generic argument for the type of the model.
     *
     * @param element The parent element.
     * @param dynamicHtmlBlock The continuation that consumes the element and a model.
     * @param <E> Type of the parent Element.
     * @param <U> Type of the model passed to the dynamic HTML block that is the same as T in this
     *     visitor.
     */
    @Override
    public <E extends Element, U> void visitDynamic(
        E element,
        BiConsumer<E, U> dynamicHtmlBlock
    ) {
        /** Creates an HtmlContinuation for the dynamic block. */
        HtmlContinuation dynamicCont = new HtmlContinuationSyncDynamic<>(
            depth,
            isClosed,
            element,
            dynamicHtmlBlock,
            this,
            new HtmlContinuationSyncCloseAndIndent(this)
        );
        /**
         * We are resolving this view for the first time. Now we just need to create an HtmlContinuation
         * corresponding to the previous static HTML, which will be followed by the dynamicCont.
         */
        chainContinuationStatic(dynamicCont);
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for the next static
         * HTML block.
         */
        indentAndAdvanceStaticBlockIndex();
    }

    @Override
    public <M, E extends Element> void visitAwait(
        E element,
        AwaitConsumer<E, M> asyncAction
    ) {
        throw new UnsupportedOperationException(
            "Await not allowed in HtmlView. Should use viewAsync() or viewSuspend() to manage" +
            " an asynchronous view."
        );
    }

    protected final void chainContinuationStatic(
        HtmlContinuation nextContinuation
    ) {
        HtmlContinuation staticCont = new HtmlContinuationSyncStatic(
            takeStaticBlock(true),
            this,
            nextContinuation
        );
        appendNode(staticCont);
        last = nextContinuation.next; // advance last to point to the new HtmlContinuationCloseAndIndent
        keepLeadingSpace = false;
    }

    /**
     * Returns the static HTML that we accumulated since the last node of the chain.
     *
     * @param trimEnd True when a dynamic block follows, which emits the trailing indentation
     *     again, and false when a value slot follows and the HTML is already final.
     */
    private String takeStaticBlock(boolean trimEnd) {
        String block = sb().substring(staticBlockIndex);
        int from = 0;
        int to = block.length();
        if (!keepLeadingSpace) while (
            from < to && block.charAt(from) <= ' '
        ) from++;
        if (trimEnd) while (to > from && block.charAt(to - 1) <= ' ') to--;
        return block.substring(from, to);
    }

    private void requireOpenTag() {
        if (isClosed) throw new IllegalStateException(
            "Cannot add attributes after!!!"
        );
    }

    /** Creates an HtmlContinuation for a value slot, keeping the HTML around it unchanged. */
    private void chainValueSlot(Kind kind, Object accessor, String attrName) {
        chainStaticThen(
            takeStaticBlock(false),
            new HtmlContinuationSyncValue(kind, accessor, attrName, this, null)
        );
    }

    /** Creates a value slot whose indentation goes into the previous static HTML block. */
    private void chainIndentedValueSlot(Kind kind, Object accessor) {
        newlineAndIndent();
        chainValueSlot(kind, accessor, null);
    }

    @Override
    public <M> void visitValueRaw(Function<M, ?> accessor) {
        chainIndentedValueSlot(Kind.RAW, accessor);
    }

    @Override
    public <M> void visitValueText(Function<M, ?> accessor) {
        chainIndentedValueSlot(Kind.TEXT, accessor);
    }

    @Override
    public <M> void visitValueInt(ToIntFunction<M> accessor) {
        chainIndentedValueSlot(Kind.INT, accessor);
    }

    @Override
    public <M> void visitValueLong(ToLongFunction<M> accessor) {
        chainIndentedValueSlot(Kind.LONG, accessor);
    }

    @Override
    public <M> void visitValueBoolean(Predicate<M> accessor) {
        chainIndentedValueSlot(Kind.BOOLEAN, accessor);
    }

    @Override
    public <M> void visitValueDouble(ToDoubleFunction<M> accessor) {
        chainIndentedValueSlot(Kind.DOUBLE, accessor);
    }

    @Override
    public <M> void visitValueAttribute(String name, Function<M, ?> accessor) {
        requireOpenTag();
        // The name and both quotes are constant, so they belong in the static blocks around the slot.
        write(SPACE);
        write(name);
        write(ATTRIBUTE_MID);
        chainValueSlot(Kind.RAW, accessor, null);
        write(QUOTATION);
    }

    @Override
    public <M> void visitValueAttributeNullable(
        String name,
        Function<M, ?> accessor
    ) {
        requireOpenTag();
        // The attribute may be absent, so name, value and quotes all wait until render time.
        chainValueSlot(Kind.ATTR_NULLABLE, accessor, name);
    }

    @Override
    public <M, E, T extends Element> void visitForEach(
        Function<M, ? extends Iterable<E>> items,
        T element,
        Consumer<T> itemTemplate
    ) {
        String outerStatic = openControlFlow();
        chainStaticThen(
            outerStatic,
            new HtmlContinuationSyncForEach<M, E>(
                items,
                recordBlock(element, itemTemplate),
                this,
                null
            )
        );
    }

    @Override
    public <M, T extends Element> void visitWhen(
        Predicate<M> condition,
        T element,
        Consumer<T> body,
        Consumer<T> orElse
    ) {
        String outerStatic = openControlFlow();
        HtmlContinuation bodyChain = recordBlock(element, body);
        HtmlContinuation elseChain = recordBlock(element, orElse);
        chainStaticThen(
            outerStatic,
            new HtmlContinuationSyncWhen<M, T>(
                condition,
                bodyChain,
                elseChain,
                this,
                null
            )
        );
    }

    /**
     * Starts a loop or a conditional and returns the static HTML that precedes it. We close the
     * begin tag of the parent here, and not inside each block, so that every block starts in
     * the same state. We have to take the static HTML before we record any block, because a
     * recording rewinds the internal string buffer.
     */
    private String openControlFlow() {
        closeParentTag();
        return takeStaticBlock(false);
    }

    /** Creates the chain of one HTML block of a loop or a conditional, bound to this visitor. */
    private <T extends Element> HtmlContinuation recordBlock(
        T element,
        Consumer<T> block
    ) {
        if (block == null) return null;
        HtmlContinuation chain = recordSubChain(element, block);
        chain.compile();
        return chain.compiledCopy(this);
    }

    /** Closes the begin tag of the parent and increments the depth, if it is still open. */
    private void closeParentTag() {
        if (isClosed) return;
        depth++;
        visitParentOnVoidElements();
    }

    /**
     * Preencodes the body in a chain of its own and leaves the internal string buffer and the
     * chain in construction as they were. We save and restore all the state of the recording,
     * which is what allows a loop or a conditional to nest inside another one.
     */
    private <T extends Element> HtmlContinuation recordSubChain(
        T element,
        Consumer<T> body
    ) {
        HtmlContinuation outerFirst = first;
        HtmlContinuation outerLast = last;
        boolean outerKeepLeadingSpace = keepLeadingSpace;
        boolean outerIsClosed = isClosed;
        int outerDepth = depth;
        int mark = sb().length();

        first = null;
        last = null;
        // The leading indentation belongs to the body, so it gets emitted on every iteration.
        keepLeadingSpace = true;
        staticBlockIndex = mark;

        body.accept(element);

        appendNode(
            new HtmlContinuationSyncStatic(takeStaticBlock(false), this, null)
        );
        HtmlContinuation chain = first;

        first = outerFirst;
        last = outerLast;
        keepLeadingSpace = outerKeepLeadingSpace;
        isClosed = outerIsClosed;
        depth = outerDepth;
        sb().setLength(mark); // the body belongs to its own chain and not to the page
        staticBlockIndex = mark;
        return chain;
    }

    /** Appends the given static HTML and then the node, without reading the buffer again. */
    private void chainStaticThen(String staticHtml, HtmlContinuation node) {
        // We skip an empty static node because the render would traverse it for nothing.
        appendNode(
            staticHtml.isEmpty()
                ? node
                : new HtmlContinuationSyncStatic(staticHtml, this, node)
        );
        last = node;
        keepLeadingSpace = true;
        staticBlockIndex = sb().length();
    }

    private void appendNode(HtmlContinuation node) {
        if (first == null) first = node; else setNext(last, node);
    }

    protected final void indentAndAdvanceStaticBlockIndex() {
        newlineAndIndent();
        staticBlockIndex = sb().length(); // increment the staticBlockIndex to the end of internal string
        // buffer.
    }

    /** Creates the last static HTML block. */
    @Override
    public void resolve(Object model) {
        String staticHtml = takeStaticBlock(true);
        HtmlContinuation staticCont = new HtmlContinuationSyncStatic(
            staticHtml,
            this,
            null
        );
        last =
            first == null
                ? first = staticCont // assign both first and last
                : setNext(last, staticCont); // append new staticCont and return it to be the new
        // last continuation.
    }

    @Override
    public final HtmlVisitor clone(boolean isIndented) {
        throw new UnsupportedOperationException(NOT_SUPPORTED_ERROR);
    }

    @SuppressWarnings({ "squid:S3011", "squid:S112" })
    public static class HtmlContinuationSetter {

        private HtmlContinuationSetter() {}

        static final Field fieldNext;

        static {
            try {
                fieldNext = HtmlContinuation.class.getDeclaredField("next");
                fieldNext.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        public static HtmlContinuation setNext(
            HtmlContinuation cont,
            HtmlContinuation next
        ) {
            try {
                fieldNext.set(cont, next);
                return next;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public final HtmlContinuation findLast() {
        HtmlContinuation node = this.first;
        while (node != null && node.next != null) {
            node = node.next;
        }
        return node;
    }
}
