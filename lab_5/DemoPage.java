import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class DemoPage {
    static final String HTML = "<!doctype html><html><head><title>Test</title></head><body>"
            + "<h1>Title</h1><p>One</p><p>Two</p><a href='#'>Link</a><br></body></html>";
    static HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/page.html", exchange -> {
            byte[] bytes = HTML.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (java.io.OutputStream out = exchange.getResponseBody()) { out.write(bytes); }
        });
        server.start(); return server;
    }
    public static void main(String[] args) throws IOException {
        start(8765);
        System.out.println("Сторінка: http://127.0.0.1:8765/page.html");
        System.out.println("Зупинити сервер: Ctrl+C");
    }
}
