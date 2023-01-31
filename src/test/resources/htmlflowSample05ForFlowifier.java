import htmlflow.*;
import org.xmlet.htmlapifaster.*;

public class Flowified {
    public static void get(StringBuilder out) {
        HtmlFlow.doc(out).setIndented(false)
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
    }
}
