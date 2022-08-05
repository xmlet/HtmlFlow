import htmlflow.*;

public class Flowified {
    public static AbstractHtmlWriter get() {
        final AbstractHtmlWriter html = StaticHtml.view().setIndented(false)
            .html()
                .head()
                    .title()
                        .text("HtmlFlow")
                    .__() //title
                .__() //head
                .body()
                    .div().attrClass("container")
                        .h1()
                            .text("My first page with HtmlFlow")
                        .__() //h1
                        .img().attrSrc("https://avatars1.githubusercontent.com/u/35267172")
                        .__() //img
                        .p()
                            .text("Typesafe is awesome! :-)")
                        .__() //p
                    .__() //div
                .__() //body
            .__() //html
            ;
        return html;
    }
}
