/*
 * MIT License
 *
 * Copyright (c) 2014-24, Miguel Gamboa (gamboa.pt)
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
import htmlflow.HtmlPage;
import htmlflow.HtmlViewAsync;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 */
public class TestFormAttributes {

    /**
     * Related with Issue https://github.com/xmlet/HtmlFlow/issues/105
     *
     * NOT solved yet. setIndent() and threadsafe() are configuration methods
     * that should not be invoked in the template function, but instead on the
     * HtmlView container.
     */
    @Test(expected = StackOverflowError.class)
    public void testTextArea() {
        // setIndentation invokes the preprocessing, which in turn will execute the
        // template function (i.e. the lambda page ->... ) and invoke again
        // the setIndentation and henceforward.
        HtmlViewAsync<Object> view = HtmlFlow.viewAsync((HtmlPage page) -> page
                .setIndented(false)
                .html()
                .body()
                .form()
                .textarea()
                .__()
                .__()
                .__());
        view.writeAsync(System.out, null).join();
    }

    /**
     * Related with Issue https://github.com/xmlet/HtmlFlow/issues/105
     */
    @Test
    public void testOnEventAttributes() {
        final String expected = "<!DOCTYPE html><html>" +
                "<body>" +
                "<button oncontextmenu=\"(event) => {}\">" +
                "</button>" +
                "</body>" +
                "</html>";
        final StringBuilder out = new StringBuilder();
        HtmlFlow
                .doc(out)
                .setIndented(false)
                .html().body().button().attrOncontextmenu("(event) => {}").__().__().__();
        assertEquals(expected, out.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckTextInterleavedInFormWithinAttributes() {
        final String style = "width: 89%; height: 77px;";
        HtmlFlow
                .doc(System.out)
                .html()
                .body()
                .form()
                .textarea()
                .addAttr("align", "right")
                .attrRows(50l)
                .attrCols(50l)
                .text("my simple texttext")
                .attrId("id")
                .attrName("name")
                .attrStyle(style)
                .__()
                .__()
                .__();
    }

}
