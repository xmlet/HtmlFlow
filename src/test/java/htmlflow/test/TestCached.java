package htmlflow.test;

import htmlflow.HtmlViewCached;
import org.junit.Test;
import org.xmlet.htmlapifaster.Body;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.H1;
import org.xmlet.htmlapifaster.Html;

public class TestCached {

    @Test
    public void cacheTest(){
        HtmlViewCached testView = new HtmlViewCached((HtmlViewCached view, Object name) -> {
            H1<Div<Body<Html<HtmlViewCached>>>> h1 = view.body().div().h1();

            view.getVisitor().visitDynamicOpen();

            h1.text(name);

            view.getVisitor().visitDynamicClose();

            h1.º().º().º().º();
        });


        System.out.println(testView.render("Luis"));
        System.out.println(testView.render("Duarte"));
    }
}
