package com.example;

public record ServerConfig(
    int port,
    int corePoolSize,
    int maxPoolSize,
    int queueSize
) {

    public ServerConfig {
        if (port < 0) {
            throw new IllegalArgumentException("port cannot be negative");
        }
        if (port > 65535) {
            throw new IllegalArgumentException("port cannot be greater than 65535");
        }
        if (corePoolSize <= 0) {
            throw new IllegalArgumentException("corePoolSize must be positive");
        }
        if (maxPoolSize <= 0) {
            throw new IllegalArgumentException("maxPoolSize must be positive");
        }
        if (queueSize <= 0) {
            throw new IllegalArgumentException("queueSize must be positive");
        }
        if (maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maxPoolSize must be greater than or equal to corePoolSize");
        }
    }

    static ServerConfig defaultConfig() {
        return new ServerConfig(
            8080,
            4,
            8,
            10
        );
    }
}
