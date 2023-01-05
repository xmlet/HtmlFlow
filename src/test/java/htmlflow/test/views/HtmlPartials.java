package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import htmlflow.HtmlTemplate;
import htmlflow.HtmlPage;
import htmlflow.test.model.Track;
import org.junit.Test;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Footer;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * These tests do not contain any assertion because they are only a sample for README.md.
 */
@SuppressWarnings("squid:S3577")
public class HtmlPartials {

    static void bbView(Footer<?> footer) {
        footer.div().text("Dummy bbView").__(); // div
    }

    static Consumer<Div<?>> footerView(Consumer<Footer<?>> banner) {
        return div -> div
            .footer()
                .of(banner::accept)
                .p()
                    .text("Created with HtmFlow")
                .__() // p
            .__(); // footer
    }

    static HtmlTemplate tracksTemplate(Consumer<Div<?>> footer) {
        return view -> view
            .div()
                .ul()
                    .<Stream<Track>>dynamic((ul, tracks) -> tracks.forEach (item -> ul
                        .li()
                            .text(item. getName ())
                        .__() // li
                    ))
                .__ () // ul
                .of(footer::accept)
            .__(); // div
    }


    /**
     * This unit test was only a sample for a paper.
     */
    @SuppressWarnings("squid:S2699")
    @Test
    public void testPartials() {

        Stream<Track> tracks = Stream.of(new Track("Space Odyssey"), new Track("Bad"), new Track("Under Pressure"));
        HtmlView tracksView = HtmlFlow.view(tracksTemplate(footerView(HtmlPartials::bbView)));
        String html = tracksView.render(tracks);
        System.out.println(html);
    }
}
