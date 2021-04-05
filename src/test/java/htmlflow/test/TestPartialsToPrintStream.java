package htmlflow.test;

import htmlflow.DynamicHtml;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static htmlflow.test.Utils.NEWLINE;
import static htmlflow.test.Utils.loadLines;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestPartialsToPrintStream {
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
        Iterator<String> actual = NEWLINE
            .splitAsStream(mem.toString())
            .iterator();
        loadLines("testAddPartialAndCheckParentPrintStream.html")
            .forEach(expected -> assertEquals(expected, actual.next()));
        assertFalse(actual.hasNext());
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
                                view.addPartial(DynamicHtml.view(this::penguinPartialTemplate), penguin)))
                    .__()
                .__() //body
              .__(); //html
    }
    private  void penguinPartialTemplate(DynamicHtml<Penguin> view, Penguin penguin){
        view
            .div()
                .attrId("data-penguin-" + penguin.getName())
                .attrStyle("display: none;")
                .addAttr("data-dna",penguin.getDnaCode());
    }
}
