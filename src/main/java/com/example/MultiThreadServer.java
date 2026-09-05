package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MultiThreadServer implements AutoCloseable {
    private static final int SERVER_STOP_DELAY_SECONDS = 5;
    private static final int EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 10;

    private final HttpServer server;
    private final ThreadPoolExecutor executor;
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();

    public MultiThreadServer(ServerConfig config) throws IOException {
        this(config, MultiThreadServer::handleHello);
    }

    public MultiThreadServer(ServerConfig config, HttpHandler handler) throws IOException {
        Objects.requireNonNull(config, "config");
        Objects.requireNonNull(handler, "handler");
        this.server = HttpServer.create(new InetSocketAddress(config.port()), 0);
        this.executor = provideExecutor(config);
        server.createContext("/hello", handler);
        server.setExecutor(executor);
    }

    public void start() {
        if (closed.get()) {
            throw new IllegalStateException("Server is already closed");
        }
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Server is already started");
        }
        server.start();
        System.out.println("Server started on port " + port());
    }

    public int port() {
        return server.getAddress().getPort();
    }

    boolean isExecutorTerminated() {
        return executor.isTerminated();
    }

    @Override
    public void close() {
        System.out.println("MultiTreadServer is shutting down...");
        if (!closed.compareAndSet(false, true)) {
            return;
        }

        server.stop(SERVER_STOP_DELAY_SECONDS);
        executor.shutdown();

        try {
            if (!executor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static ThreadPoolExecutor provideExecutor(ServerConfig config) {
        return new ThreadPoolExecutor(
            config.corePoolSize(),
            config.maxPoolSize(),
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(config.queueSize()),
            new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private static void handleHello(HttpExchange exchange) throws IOException {
        byte[] response = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        try (exchange; OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(response);
        }
    }
}
