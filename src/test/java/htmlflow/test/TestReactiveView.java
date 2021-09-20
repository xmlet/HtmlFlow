/*
 * MIT License
 *
 * Copyright (c) 2014-21, Miguel Gamboa (htmlflow.org)
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

import htmlflow.DynamicHtml;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestReactiveView {

    @Test
    public void testAddPartialAndCheckParentPrintStream() {
        /**
         * Only for unit tests purpose and control completion
         */
        CompletableFuture<Void> cf = new CompletableFuture<>();
        /**
         * Cont from 1 to 5 with 0 delay in 10 milliseconds interval.
         */
        Observable<Long> nrs = Observable
            .intervalRange(1, 5, 0, 10, TimeUnit.MILLISECONDS)
            .doOnComplete(() -> cf.complete(null));
        DynamicHtml<Observable<Long>> view = DynamicHtml.view(System.out, TestReactiveView::RxViewWithListingFromObservable);
        /**
         * Act
         */
        view.write(nrs); // render view and emit output
        cf.join();       // Wait for nrs emit completion
    }

    private static void RxViewWithListingFromObservable(DynamicHtml<Observable<Long>> view, Observable<Long> model) {
        view
            .html()
                .head().title().text("Reactive Test").__().__()
                .body()
                    .div()
                        .p().text("Creating a listing from a reactive model: ").__()
                        .ul()
                            .dynamic(ul -> model.forEach(nr -> ul
                                .li().text(nr).__()
                            ))
                        .__()
                    .__()
                .__()
            .__();
    }

}
