package htmlflow.test;

import htmlflow.DynamicHtml;
import org.junit.Assert;
import org.junit.Test;

public class TestCached {

    static void testView(DynamicHtml<String> view, String name) {
            view
                .html()
                    .body()
                        .div()
                            .h1()
                                .of(h1 -> h1.text(name))
                            .º()
                        .º()
                    .º()
                .º();
    }

    @Test
    public void cacheTest(){


        String expected1 =  "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "\t<body>\n" +
                            "\t\t<div>\n" +
                            "\t\t\t<h1>\n" +
                            "\t\t\t\tLuis\n" +
                            "\t\t\t</h1>\n" +
                            "\t\t</div>\n" +
                            "\t</body>\n" +
                            "</html>";

        String expected2 =  "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "\t<body>\n" +
                            "\t\t<div>\n" +
                            "\t\t\t<h1>\n" +
                            "\t\t\t\tDuarte\n" +
                            "\t\t\t</h1>\n" +
                            "\t\t</div>\n" +
                            "\t</body>\n" +
                            "</html>";

        DynamicHtml<String> testView = DynamicHtml.view(TestCached::testView);
        Assert.assertEquals(testView.render("Luis"), expected1);
        Assert.assertEquals(testView.render("Duarte"), expected2);
    }
}
