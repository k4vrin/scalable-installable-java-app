package com.example;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        MultiThreadServer server = new MultiThreadServer(ServerConfig.defaultConfig());
        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "http-server-shutdown"));
        server.start();
    }
}
