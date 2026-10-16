package htmlflow.test.e2e;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/** Minimal server for MFE e2e tests: serves the host page and the MFE fragments. */
public final class MfeTestServer {

    private final HttpServer server;
    // Lets a slow streaming response run without blocking other requests
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public MfeTestServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        this.server.setExecutor(executor);
    }

    public MfeTestServer html(String path, Supplier<String> body) {
        server.createContext(path, ex -> {
            // HttpServer contexts match by prefix, so enforce an exact path match
            if (!ex.getRequestURI().getPath().equals(path)) {
                send(ex, 404, "text/plain", "Not found".getBytes(StandardCharsets.UTF_8));
                return;
            }
            send(ex, 200, "text/html; charset=utf-8", body.get().getBytes(StandardCharsets.UTF_8));
        });
        return this;
    }

    /**
     * Streams {@code chunks} as separate HTTP chunks (chunked transfer encoding), flushing each one and
     * pausing {@code delay} before the next, so the browser receives them as separate reads.
     */
    public MfeTestServer stream(String path, Duration delay, String... chunks) {
        server.createContext(path, ex -> {
            if (!ex.getRequestURI().getPath().equals(path)) {
                send(ex, 404, "text/plain", "Not found".getBytes(StandardCharsets.UTF_8));
                return;
            }
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                for (int i = 0; i < chunks.length; i++) {
                    if (i > 0) Thread.sleep(delay.toMillis());
                    os.write(chunks[i].getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        return this;
    }

    public MfeTestServer start() {
        server.start();
        return this;
    }

    public String url(String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }

    private static void send(HttpExchange ex, int status, String type, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    public void stop() {
        server.stop(0);
        executor.shutdownNow();
    }
}
