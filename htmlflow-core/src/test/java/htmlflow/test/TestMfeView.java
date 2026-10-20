package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlMfeConfig;
import htmlflow.HtmlView;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


public class TestMfeView {

    @Test
    public void shouldRenderMicroFrontend() {
        HtmlView<?> mfe = HtmlFlow.ViewFactory.builder().mfeEnabled(true).build().view((page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe(cfg -> {
                        cfg.setMfeUrlResource("http://localhost:8081/bikes");
                        cfg.setMfeName("mfe1");
                        cfg.setMfeListeningEventName("triggerBikeEvent");
                        cfg.setMfeElementName("some-element");
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
        HtmlView<?> mfe = HtmlFlow.ViewFactory.builder().mfeEnabled(true).preEncoding(true).build().view(page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe(cfg -> {
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

    @Test(expected = IllegalStateException.class)
    public void shouldNotRenderStreamingMicroFrontend() {
        HtmlFlow.ViewFactory.builder().mfeEnabled(true).preEncoding(false).build().view(page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe(cfg -> {
                        cfg.setMfeUrlResource("someurl");
                    })
                    .__()
                    .__()
                    .__();
        });
    }

    @Test
    public void shouldBuildMfeConfigWithSharedStylingAndIntegrity() {
        HtmlMfeConfig.Builder builder = new HtmlMfeConfig.Builder()
                .setMfeUrlResource("http://localhost:8081/bikes")
                .setMfeName("mfe1")
                .setMfeSharedStylingUrl("http://localhost:8081/css/shared.css")
                .setMfeScriptIntegrity("sha384-abc");
        assertEquals("http://localhost:8081/bikes", builder.getMfeUrlResource());
        assertEquals("mfe1", builder.getMfeName());
        HtmlMfeConfig cfg = builder.build();
        assertEquals("http://localhost:8081/css/shared.css", cfg.getMfeSharedStylingUrl());
        assertEquals("sha384-abc", cfg.getMfeScriptIntegrity());
    }

    @Test
    public void shouldFailBuildingMfeConfigWithoutRequiredFields() {
        assertThrows(IllegalStateException.class,
                () -> new HtmlMfeConfig.Builder().setMfeName("mfe1").build());
        assertThrows(IllegalStateException.class,
                () -> new HtmlMfeConfig.Builder().setMfeUrlResource(" ").setMfeName("mfe1").build());
        assertThrows(IllegalStateException.class,
                () -> new HtmlMfeConfig.Builder().setMfeUrlResource("url").build());
        assertThrows(IllegalStateException.class,
                () -> new HtmlMfeConfig.Builder().setMfeUrlResource("url").setMfeName(" ").build());
    }

    @Test
    public void shouldNotRenderEmptyStylingAttributes() {
        String actual = HtmlFlow.ViewFactory.builder().mfeEnabled(true).build().view(page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe(cfg -> {
                        cfg.setMfeUrlResource("http://localhost:8081/bikes");
                        cfg.setMfeName("mfe1");
                        cfg.setMfeStylingUrl("");
                        cfg.setMfeSharedStylingUrl("");
                    })
                    .__()
                    .__()
                    .__();
        }).render();
        assertTrue(actual.contains("mfe-name=\"mfe1\""));
        assertFalse(actual.contains("mfe-styling-url"));
        assertFalse(actual.contains("mfe-shared-styling-url"));
    }

    @Test
    public void shouldRenderScriptWithIntegrity() {
        String actual = HtmlFlow.ViewFactory.builder().mfeEnabled(true).build().view(page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe(cfg -> {
                        cfg.setMfeUrlResource("http://localhost:8081/bikes");
                        cfg.setMfeName("mfe1");
                        cfg.setMfeScriptUrl("http://localhost:8081/js/mfe-bikes.js");
                        cfg.setMfeScriptIntegrity("sha384-abc");
                    })
                    .__()
                    .__()
                    .__();
        }).render();
        String script = "<script type=\"module\" src=\"http://localhost:8081/js/mfe-bikes.js\""
                + " integrity=\"sha384-abc\" crossorigin=\"anonymous\"></script>";
        assertTrue(actual.contains(script + "</head>"));
    }

    private void assertContents(String pathToExpected, String actual) {
        String normalizedActual = actual.replaceAll("\\s+", "");
        String expected = Utils.loadLines(pathToExpected)
                .collect(Collectors.joining(""))
                .replaceAll("\\s+", "");
        assertEquals(expected, normalizedActual);
    }

}
