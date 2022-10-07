/*
 * MIT License
 *
 * Copyright (c) 2014-22, mcarvalho (gamboa.pt) and lcduarte (github.com/lcduarte)
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
package htmlflow.async.nodes;

import org.xmlet.htmlapifaster.Element;

import java.util.function.Supplier;

/**
 * Represents a specification of a {@link ContinuationNode} which holds synchronous continuations represented by the {@code action}
 * {@link Supplier}
 *
 * @author Pedro Fialho
 * @param <E> The current Html element being processed
 */
public class ThenNode<E extends Element> extends ContinuationNode {
    private final Supplier<E> action;
    
    public ThenNode(Supplier<E> action) {
        this.action = action;
    }
    
    @Override
    public void execute() {
        this.action.get();
        final ContinuationNode next = this.next;
        if (next != null) {
            next.execute();
        }
    }

}
