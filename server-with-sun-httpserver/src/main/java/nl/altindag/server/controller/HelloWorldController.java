package nl.altindag.server.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloWorldController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (OutputStream responseBody = exchange.getResponseBody()) {

            exchange.getResponseHeaders().set("Content-Type", "text/plain");

            var payload = "Hello";
            exchange.sendResponseHeaders(200, payload.length());
            responseBody.write(payload.getBytes(StandardCharsets.UTF_8));
        }
    }

}
