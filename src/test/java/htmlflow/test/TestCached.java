package htmlflow.test;

import htmlflow.HtmlViewCached;
import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlapifaster.Element;
import org.xmlet.htmlapifaster.Html;

public class TestCached {

    @Test
    public void cacheTest(){
        HtmlViewCached<Html<Element>> testView = HtmlViewCached.html((HtmlViewCached<Html<Element>> view, Object name) ->
            view.getRoot()
                .body()
                    .div()
                        .h1()
                            .of(h1 -> h1.text(name))
                        .º()
                    .º()
                .º()
            .º()
        );

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

        Assert.assertEquals(testView.render("Luis"), expected1);
        Assert.assertEquals(testView.render("Duarte"), expected2);
    }
}
