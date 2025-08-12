package htmlflow.test;

import htmlflow.HtmlFlow;
import htmlflow.HtmlView;
import org.junit.Assert;
import org.junit.Test;


public class TestMfeView {

    @Test
    public void shouldRenderMicroFrontend() {
        HtmlView<?> mfe = HtmlFlow.mfe(page -> {
            page.html()
                    .head().__()
                        .body()
                            .div()
                                .mfe((cfg)-> {
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
        });
        String actual = mfe.render();
        Assert.assertEquals("<!DOCTYPE html><html><head><script type=\"module\" src=\"base.js\"></script><script type=\"module\" src=\"http://localhost:8081/js/mfe-bikes.js\"></script></head><body><div><micro-frontend mfe-url=\"http://localhost:8081/bikes\" mfe-name=\"mfe1\" mfe-styling-url=\"http://localhost:8081/css/style.css\" mfe-listen-event=\"triggerBikeEvent\" mfe-trigger-event=\"triggerCartEvent\"></micro-frontend></div></body></html>", actual);
    }

    @Test
    public void shouldRenderStreamingMicroFrontend() {
        HtmlView<?> mfe = HtmlFlow.mfe(page -> {
            page.html()
                    .head().__()
                    .body()
                    .div()
                    .mfe((cfg)-> {
                        cfg.setMfeUrlResource("http://localhost:8080/html-chunked/stream");
                        cfg.setMfeName("mfe2");
                        cfg.setMfeListeningEventName("triggerStreamEvent");
                        cfg.setMfeStreamingData(true);
                    })
                    .__()
                    .__()
                    .__();
        });
        String html = mfe.render();
        Assert.assertEquals("<!DOCTYPE html><html><head><script type=\"module\" src=\"base.js\"></script></head><body><div><micro-frontend mfe-url=\"http://localhost:8080/html-chunked/stream\" mfe-name=\"mfe2\" mfe-styling-url=\"null\" mfe-listen-event=\"triggerStreamEvent\" mfe-trigger-event=\"null\" mfe-stream-data=\"true\"></micro-frontend></div></body></html>", html);
    }
}
