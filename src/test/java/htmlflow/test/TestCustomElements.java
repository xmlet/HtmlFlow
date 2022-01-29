package htmlflow.test;

import htmlflow.test.views.HtmlCustomElements;
import org.junit.Test;

import java.util.Iterator;

import static htmlflow.test.Utils.NEWLINE;
import static org.junit.Assert.assertEquals;

public class TestCustomElements {

    @Test
    public void testStaticViewWithCustomElements() {
        String actual = HtmlCustomElements.customElements.render();
        assertLines("customElements.html", actual);
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
//                    System.out.println(line);
                    assertEquals(expected, line);
                });
    }
}
