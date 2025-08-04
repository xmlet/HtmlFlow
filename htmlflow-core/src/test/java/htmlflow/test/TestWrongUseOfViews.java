/*
 * MIT License
 *
 * Copyright (c) 2014-18, mcarvalho (gamboa.pt)
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

package htmlflow.test;

import htmlflow.HtmlFlow;
import org.junit.Assert;
import org.junit.Test;

public class TestWrongUseOfViews {

    /**
     * A StaticHtml view should not use dynamic().
     * LocalDate is the model type in this test.
     */
    @Test(expected = IllegalStateException.class)
    public void testWrongUseOfDynamicInStaticHtml(){
        HtmlFlow.doc(System.out)
            .html()
                .head()
                    .title().text("Task Details").__()
                    .dynamic((head, model) -> {
                        Assert.fail("It should not reach here!");
                    });
    }

    /**
     * A HtmlDoc cannot be set to thread-safety.
     */
    @Test(expected = IllegalStateException.class)
    public void testWrongUseOfThreadSafeInViewWithPrintStream(){
        HtmlFlow
            .doc(System.out)
            .threadSafe();
    }

}
