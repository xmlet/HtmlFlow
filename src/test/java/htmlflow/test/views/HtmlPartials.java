package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import htmlflow.HtmlTemplate;
import htmlflow.HtmlPage;
import htmlflow.test.model.Track;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * These tests do not contain any assertion because they are only a samplefor README.md.
 */
@SuppressWarnings("squid:S3577")
public class HtmlPartials {

    static void bbView(HtmlPage view) {
        view.div().text("Dummy bbView").__(); // div
    }

    static HtmlView footerView(Consumer<HtmlPage> banner) {
        return HtmlFlow.view((view, model) -> view
            .div()
                .of(__ -> banner.accept(view))
                .p()
                    .text("Created with HtmFlow")
                .__() // p
            .__() // div
        ); //
    }


    /**
     * This unit test was only a sample for a paper.
     */
    @SuppressWarnings("squid:S2699")
    @Test
    public void testPartials() {
        HtmlTemplate<Stream<Track>> tracksTemplate = (view , tracks, partials) -> view
        .div()
            .ul()
                .of(ul -> tracks.forEach (item -> ul
                    .li()
                        .text(item. getName ())
                    .__() // li
                ))
            .__ () // ul
            .of(__ -> view.addPartial(partials[0]))
        .__(); // div


        Stream<Track> tracks = Stream.of(new Track("Space Odyssey"), new Track("Bad"), new Track("Under Pressure"));
        HtmlView<Stream<Track>> tracksView = HtmlFlow.view(tracksTemplate);
        String html = tracksView.render(tracks, footerView(HtmlPartials::bbView));
        System.out.println(html);
    }
}
