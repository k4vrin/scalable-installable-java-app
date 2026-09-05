package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiThreadServerTest {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

    @Test
    void defaultExecutorSerializesBlockedRequests() throws Exception {
        BlockingHandler handler = new BlockingHandler();
        HttpServer baseline = HttpServer.create(new InetSocketAddress(0), 0);
        baseline.createContext("/hello", handler);
        baseline.setExecutor(null);
        baseline.start();

        try (HttpClient client = HttpClient.newHttpClient()) {
            CompletableFuture<HttpResponse<String>> first = send(client, baseline.getAddress().getPort());
            assertTrue(handler.firstEntered.await(1, TimeUnit.SECONDS));

            CompletableFuture<HttpResponse<String>> second = send(client, baseline.getAddress().getPort());
            assertFalse(handler.bothEntered.await(250, TimeUnit.MILLISECONDS));

            handler.release.countDown();
            assertSuccessful(first);
            assertSuccessful(second);
        } finally {
            handler.release.countDown();
            baseline.stop(0);
        }
    }

    @Test
    void boundedExecutorRunsBlockedRequestsConcurrently() throws Exception {
        BlockingHandler handler = new BlockingHandler();
        MultiThreadServer server = new MultiThreadServer(new ServerConfig(0, 4, 8, 10), handler);
        server.start();

        try (server; HttpClient client = HttpClient.newHttpClient()) {
            CompletableFuture<HttpResponse<String>> first = send(client, server.port());
            assertTrue(handler.firstEntered.await(1, TimeUnit.SECONDS));

            CompletableFuture<HttpResponse<String>> second = send(client, server.port());
            assertTrue(handler.bothEntered.await(1, TimeUnit.SECONDS));

            handler.release.countDown();
            assertSuccessful(first);
            assertSuccessful(second);
        } finally {
            handler.release.countDown();
        }

        assertTrue(server.isExecutorTerminated());
    }

    @Test
    void closeIsIdempotentAndPreventsRestart() throws Exception {
        MultiThreadServer server = new MultiThreadServer(new ServerConfig(0, 1, 1, 1));
        server.start();
        
        server.close();
        // testing close idempotency
        server.close();

        assertTrue(server.isExecutorTerminated());
        assertThrows(IllegalStateException.class, server::start);
    }

    private static CompletableFuture<HttpResponse<String>> send(HttpClient client, int port) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:" + port + "/hello"))
            .timeout(REQUEST_TIMEOUT)
            .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void assertSuccessful(CompletableFuture<HttpResponse<String>> response) throws Exception {
        HttpResponse<String> result = response.get(REQUEST_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        assertEquals(200, result.statusCode());
        assertEquals("Hello, World!", result.body());
    }

    private static final class BlockingHandler implements HttpHandler {
        private final CountDownLatch firstEntered = new CountDownLatch(1);
        private final CountDownLatch bothEntered = new CountDownLatch(2);
        private final CountDownLatch release = new CountDownLatch(1);

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            firstEntered.countDown();
            bothEntered.countDown();
            try {
                release.await();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                exchange.close();
                return;
            }

            byte[] response = "Hello, World!".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (exchange; OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(response);
            }
        }
    }
}
