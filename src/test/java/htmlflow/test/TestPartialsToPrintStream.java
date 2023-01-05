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

import htmlflow.HtmlFlow;
import htmlflow.HtmlPage;
import htmlflow.HtmlView;
import htmlflow.continuations.HtmlContinuation;
import htmlflow.visitor.HtmlVisitor;
import org.junit.Test;
import org.xmlet.htmlapifaster.Div;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestPartialsToPrintStream {

    final static int MB = 1024*1024;

    @Test
    public void testAddPartialAndCheckParentPrintStream() {
        /**
         * Arrange
         */
        Penguin [] penguins = {
            new Penguin("Albert", "9459-efgerfg-4irorgo"),
            new Penguin("Joseph", "2487i4rf-39fhfh-4yth")
        };
        ByteArrayOutputStream mem = new ByteArrayOutputStream();
        /**
         * Act
         */
        HtmlFlow
            .view(new PrintStream(mem), this::template)
            .write(Stream.of(penguins));
        /**
         * Assert
         */
        Iterator<String> actual = Utils
            .NEWLINE
            .splitAsStream(mem.toString())
            .iterator();
        Utils
            .loadLines("testAddPartialAndCheckParentPrintStream.html")
            .forEach(expected -> assertEquals(expected, actual.next()));
        assertFalse(actual.hasNext());
    }

    /**
     * Discards all bytes through the PrintStream.
     * Testing with large data model to check that partial view is not internally buffering strings and
     * is not incurring in OutOfMemoryError.
     */
    @Test
    public void testAddPartialWithLargeData() throws NoSuchFieldException, IllegalAccessException {
        /**
         * Arrange
         */
        Stream<Penguin> penguins = IntStream
            .range(0, 128)
            .mapToObj(n -> new Penguin("Albert" + n, getSaltString(1 * MB)));
        /**
         * Act
         */
        // Discards all bytes through the PrintStream.
        HtmlView view = HtmlFlow.view(new PrintStream(nullOutputStream()), this::template);
        view.write(penguins);
        /**
         * Assert
         */
        HtmlVisitor visitor = view.getVisitor();
        Field firstField = visitor.getClass().getDeclaredField("first");
        firstField.setAccessible(true);
        HtmlContinuation node = (HtmlContinuation) firstField.get(visitor);
        int count = 0;
        while(node != null) {
            count++;
            node = node.getNext();
        }
        assertEquals(4, count); // one more continuation for HtmlContinuationCloseAndIndent
    }

    static  String getSaltString(int length) {
        char[] SALTCHARS= "ABCDEFGHIJKLMNOPQRSTUVWXYZ ".toCharArray();
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = rnd.nextInt(SALTCHARS.length);
            salt.append(SALTCHARS[index]);
        }
        return salt.toString();
    }

    static class Penguin {
        final String name;
        final String dnaCode;
        Penguin(String name, String dnaCode) {
            this.name = name;
            this.dnaCode = dnaCode;
        }
        public String getName() { return name; }

        public String getDnaCode() { return dnaCode; }
    }

    private void template(HtmlPage page) {
        page.html()
                .head()
                    .title().text("MyPenguinExample").__()
                .__() //head
                .body()
                    .div().attrClass("local-storage").attrStyle("display: none;")
                        .<Stream<Penguin>>dynamic((div, model) ->
                            model.forEachOrdered(penguin -> penguimPartialView(div, penguin)))
                    .__()
                .__() //body
              .__(); //html
    }

    private final void penguimPartialView (Div<?> div, Penguin penguin) {
        div
            .div()
                .attrId("data-penguin-" + penguin.getName())
                .attrStyle("display: none;")
                .addAttr("data-dna",penguin.getDnaCode())
            .__();
    }

    /**
     * Copied from JDK 11
     */
    private static OutputStream nullOutputStream() {
        return new OutputStream() {
            private volatile boolean closed;

            private void ensureOpen() throws IOException {
                if (closed) {
                    throw new IOException("Stream closed");
                }
            }

            @Override
            public void write(int b) throws IOException {
                ensureOpen();
            }

            @Override
            public void write(byte b[], int off, int len) throws IOException {
                checkFromIndexSize(off, len, b.length, null);
                ensureOpen();
            }

            @Override
            public void close() {
                closed = true;
            }
        };
    }
    public static <X extends RuntimeException>
    int checkFromIndexSize(int fromIndex, int size, int length,
                           BiFunction<String, List<Integer>, X> oobef) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex)
            throw new IndexOutOfBoundsException();
        return fromIndex;
    }
}
