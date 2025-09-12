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
import htmlflow.continuations.HtmlContinuationAsync;
import htmlflow.continuations.HtmlContinuationSyncCloseAndIndent;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

/**
 * @author Pedro Fialho
 */
public class PreprocessingVisitorAsync extends PreprocessingVisitor {

    public PreprocessingVisitorAsync(boolean isIndented) {
        super(isIndented);
    }

    @Override
    public <M, E extends Element> void visitAwait(
        E element,
        AwaitConsumer<E, M> asyncHtmlBlock
    ) {
        /** Creates an HtmlContinuation for the async block. */
        HtmlContinuation asyncCont = new HtmlContinuationAsync<>(
            depth,
            isClosed,
            element,
            asyncHtmlBlock,
            this,
            new HtmlContinuationSyncCloseAndIndent(this)
        );
        /**
         * We are resolving this view for the first time. Now we just need to create an HtmlContinuation
         * corresponding to the previous static HTML, which will be followed by the asyncCont.
         */
        chainContinuationStatic(asyncCont);
        /**
         * We have to run newlineAndIndent to leave isClosed and indentation correct for the next static
         * HTML block.
         */
        indentAndAdvanceStaticBlockIndex();
    }

    @Override
    public <M, E extends Element> void visitSuspending(
        E element,
        SuspendConsumer<E, M> suspendAction
    ) {
        throw new UnsupportedOperationException(
            "Illegal use of suspending builder. To use it you should create a" +
            " HtmlViewSuspend."
        );
    }
}
