package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class TestMfeView {

    @Test
    public void shouldRenderMicroFrontend() {
        HtmlView<?> mfe = HtmlFlow.ViewFactory.builder().mfeEnabled(true).build().view((page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe((cfg) -> {
                        cfg.setMfeUrlResource("http://localhost:8081/bikes");
                        cfg.setMfeName("mfe1");
                        cfg.setMfeListeningEventName("triggerBikeEvent");
                        cfg.setMfeTriggersEventName("triggerCartEvent");
                        cfg.setMfeScriptUrl("http://localhost:8081/js/mfe-bikes.js");
                        cfg.setMfeStylingUrl("http://localhost:8081/css/style.css");
                    })
                    .__()
                    .__()
                    .__();
        }));
        String actual = mfe.render();
        assertContents("mfeView.html", actual);
    }

    @Test
    public void shouldRenderStreamingMicroFrontend() {
        HtmlView<?> mfe = HtmlFlow.ViewFactory.builder().mfeEnabled(true).build().view(page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe((cfg) -> {
                        cfg.setMfeUrlResource("http://localhost:8080/html-chunked/stream");
                        cfg.setMfeName("mfe2");
                        cfg.setMfeListeningEventName("triggerStreamEvent");
                        cfg.setMfeStreamingData(true);
                    })
                    .__()
                    .__()
                    .__();
        });
        String actual = mfe.render();
        assertContents("mfeStreamingView.html", actual);
    }

    private void assertContents(String pathToExpected, String actual) {
        String normalizedActual = actual.replaceAll("\\s+", "");
        String expected = Utils.loadLines(pathToExpected)
                .collect(Collectors.joining(""))
                .replaceAll("\\s+", "");
        assertEquals(expected, normalizedActual);
    }

}
