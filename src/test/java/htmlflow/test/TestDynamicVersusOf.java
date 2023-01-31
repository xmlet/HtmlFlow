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

import htmlflow.test.model.Stock;
import htmlflow.test.views.HtmlDynamicStocks;
import org.junit.Test;

import java.util.Iterator;

import static htmlflow.test.Utils.NEWLINE;
import static org.junit.Assert.assertEquals;

public class TestDynamicVersusOf {

    @Test
    public void testRenderRightDynamicWithTwoDifferentModels(){
        /*
         * First render with Stock.dummy3Items()
         */
        String actual = HtmlDynamicStocks
            .stocksViewOk
            .render(Stock.dummy3Items());
        assertLines("stocks3items.html", actual);
        /*
         * Then render with Stock.dummy5Items()
         */
        actual = HtmlDynamicStocks
            .stocksViewOk
            .render(Stock.other3Items());
        assertLines("stocks3others.html", actual);
    }

    @Test
    public void testWriteRightDynamicWithTwoDifferentModels(){
        /*
         * First render with Stock.dummy3Items()
         */
        StringBuilder actual1 = new StringBuilder();
        HtmlDynamicStocks
            .stocksViewOk
            .setOut(actual1)
            .write(Stock.dummy3Items());
        assertLines("stocks3items.html", actual1.toString());
        /*
         * Then render with Stock.dummy5Items()
         */
        StringBuilder actual2 = new StringBuilder();
        HtmlDynamicStocks
            .stocksViewOk
            .setOut(actual2)
            .render(Stock.other3Items());
        assertLines("stocks3others.html", actual2.toString());
    }

    private static void assertLines(String pathToExpected, String actual) {
        Iterator<String> iter = NEWLINE
            .splitAsStream(actual)
            .map(String::toLowerCase)
            .iterator();
        Utils
            .loadLines(pathToExpected)
            .map(String::toLowerCase)
            .forEach(expected -> {
                String line = iter.next();
                // System.out.println(line);
                assertEquals(expected, line);
            });
    }
}
