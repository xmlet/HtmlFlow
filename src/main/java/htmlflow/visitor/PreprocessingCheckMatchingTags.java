/*
 * MIT License
 *
 * Copyright (c) 2014-2025, Miguel Gamboa (gamboa.pt)
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

import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.ElementVisitor;
import org.xmlet.htmlapifaster.SuspendConsumer;
import org.xmlet.htmlapifaster.Text;
import org.xmlet.htmlapifaster.async.AwaitConsumer;

import java.util.function.BiConsumer;

public class PreprocessingCheckMatchingTags extends ElementVisitor {
    private int openTags = 0;

    public int getOpenTags() {
        return openTags;
    }

    @Override
    public void visitElement(Element var1) {
        openTags++;
    }

    @Override
    public void visitAttribute(String var1, String var2) {
    }

    @Override
    public void visitAttributeBoolean(String name, String value) {

    }

    @Override
    public void visitParent(Element var1) {
        openTags--;
        throw new IllegalStateException("Closing unmatched tag!");
    }

    @Override
    public <R> void visitText(Text<? extends Element, R> var1) {

    }

    @Override
    public <R> void visitRaw(Text<? extends Element, R> txt) {

    }

    @Override
    public <R> void visitComment(Text<? extends Element, R> var1) {

    }

    @Override
    public <E extends Element, U> void visitDynamic(E element, BiConsumer<E, U> consumer) {

    }

    @Override
    public <M, E extends Element> void visitAwait(E element, AwaitConsumer<E, M> asyncAction) {

    }

    @Override
    public <M, E extends Element> void visitSuspending(E element, SuspendConsumer<E, M> suspendAction) {

    }
}
