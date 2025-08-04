package htmlflow.test.views;

import htmlflow.HtmlFlow;
import htmlflow.HtmlTemplate;
import htmlflow.HtmlView;
import htmlflow.HtmlViewAsync;
import htmlflow.test.model.Track;
import org.junit.Test;
import org.xmlet.htmlapifaster.Div;
import org.xmlet.htmlapifaster.Footer;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.Duration.ofMillis;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

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

        Stream<Track> tracks = Stream.of(new Track("David Bowie", "Space Oddity", LocalDate.of(2016, 1, 10)), new Track("U2", "Bad"), new Track("Queen", "Under Pressure"));
        HtmlView<Stream<Track>> tracksView = HtmlFlow.view(tracksTemplate(footerView(HtmlPartials::bbView)));
        String html = tracksView.render(tracks);
        System.out.println(html);
    }

    /**
     * Sample showcase of data binding with HtmlDoc
     */
    private void trackDoc(Appendable out, Track track) {
        HtmlFlow.doc(out)
                .html()
                    .body()
                        .ul()
                            .li().text(format("Artist: %s", track.getArtist())).__()
                            .li().text(format("Track: %s", track.getName())).__() // li
                            .of(ul -> {
                                if(track.getDiedDate() != null)
                                    ul.li().text(format("Died in %d", track.getDiedDate().getYear())).__();
                            })
                        .__() // ul
                    .__() // body
                .__(); // html
    }

    /**
     * Sample showcase of data binding with HtmlView
     */
    @Test
    public void trackView() {
        HtmlView<Track> trackView = HtmlFlow.view(view -> view
                .html()
                    .body()
                        .ul()
                            .<Track>dynamic((ul, track) -> {
                                ul
                                    .li().text(format("Artist: %s", track.getArtist())).__()
                                    .li().text(format("Track: %s", track.getName())).__();
                                if(track.getDiedDate() != null)
                                    ul.li().text(format("Died in %d", track.getDiedDate().getYear())).__();
                            })
                        .__() // ul
                    .__() // body
                .__() // html
        );
        final Track spaceOddity = new Track("David Bowie", "Space Oddity", LocalDate.of(2016, 1, 10));
        final StringBuilder actual = new StringBuilder();
        trackDoc(actual, spaceOddity);
        assertEquals(actual.toString(), trackView.render(spaceOddity));
    }

    private void playlistDoc(Appendable out, List<Track> tracks) {
        HtmlFlow.doc(out)
            .html()
                .body()
                    .table()
                        .tr()
                            .th().text("Artist").__()
                            .th().text("Track").__()
                        .__()
                        .of(table -> tracks.forEach( trk ->
                            table
                                .tr()
                                    .td().text(trk.getArtist()).__()
                                    .td().text(trk.getName()).__()
                                .__()
                        ))
                    .__()
                .__()
            .__();
    }
    /**
     * Sample showcase of loop with HtmlView
     */
    @Test
    public void playlistView() {
        HtmlView<List<Track>> playlistView = HtmlFlow.view(view -> view
            .html()
                .body()
                    .table()
                        .tr()
                            .th().text("Artist").__()
                            .th().text("Track").__()
                        .__()
                        .<List<Track>>dynamic((table, tracks) -> tracks.forEach( trk ->
                            table
                                .tr()
                                    .td().text(trk.getArtist()).__()
                                    .td().text(trk.getName()).__()
                                .__()
                        ))
                    .__()
                .__()
            .__()
        );
        List<Track> tracks = asList(new Track("David Bowie", "Space Oddity", LocalDate.of(2016, 1, 10)), new Track("U2", "Bad"), new Track("Queen", "Under Pressure"));
        final StringBuilder actual = new StringBuilder();
        playlistDoc(actual, tracks);
        assertEquals(actual.toString(), playlistView.render(tracks));
    }

    /**
     * Sample showcase of loop with HtmlView
     */
    @Test
    public void playlistViewAsync() {
        HtmlViewAsync<Flux<Track>> playlistView = HtmlFlow.viewAsync(view -> view
                .html()
                .body()
                .table()
                .tr()
                .th().text("Artist").__()
                .th().text("Track").__()
                .__()
                .<Flux<Track>>await((table, tracks, onCompletion) -> tracks
                        .doOnComplete(onCompletion::finish)
                        .doOnNext( trk ->
                            table
                                .tr()
                                .td().text(trk.getArtist()).__()
                                .td().text(trk.getName()).__()
                                .__()
                ))
                .__()
                .__()
                .__()
        );
        List<Track> tracks = asList(new Track("David Bowie", "Space Oddity", LocalDate.of(2016, 1, 10)), new Track("U2", "Bad"), new Track("Queen", "Under Pressure"));
        Flux<Track> tracksFlux = Flux
                .fromIterable(tracks)
                .delayElements(ofMillis(10));
        final StringBuilder expected = new StringBuilder();
        playlistDoc(expected, tracks);
        playlistView.renderAsync(tracksFlux).thenAccept(actual -> {
            assertEquals(expected.toString(), actual);
        });
    }
}
