package htmlflow.test;

import htmlflow.DynamicHtml;
import htmlflow.HtmlView;
import htmlflow.HtmlVisitorCache;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.io.OutputStream.*;
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
        DynamicHtml
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
        DynamicHtml<Stream<Penguin>> view = DynamicHtml.view(new PrintStream(nullOutputStream()), this::template);
        view.write(penguins);
        /**
         * Assert
         */
        HtmlVisitorCache visitor = view.getVisitor();
        Field blocksField = visitor.getClass().getSuperclass().getDeclaredField("cacheBlocksList");
        blocksField.setAccessible(true);
        List blocks = (List) blocksField.get(visitor);
        assertEquals(2, blocks.size());
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

    private void template(DynamicHtml<Stream<Penguin>> view, Stream<Penguin> model) {
        view.html()
                .head()
                    .title().text("MyPenguinExample").__()
                .__() //head
                .body()
                    .div().attrClass("local-storage").attrStyle("display: none;")
                        .dynamic(localStorageDiv ->
                            model.forEachOrdered(penguin ->
                                view.addPartial(penguimPartialView, penguin)))
                    .__()
                .__() //body
              .__(); //html
    }

    private final HtmlView<Penguin> penguimPartialView = DynamicHtml.view((view, penguin) -> {
        view
            .div()
                .attrId("data-penguin-" + penguin.getName())
                .attrStyle("display: none;")
                .addAttr("data-dna",penguin.getDnaCode());
    });
}
