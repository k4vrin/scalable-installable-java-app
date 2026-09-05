package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServerConfigTest {
    @Test
    void acceptsEphemeralPortAndFiniteLimits() {
        assertDoesNotThrow(() -> new ServerConfig(0, 4, 8, 10));
    }

    @Test
    void rejectsInvalidPortAndExecutorLimits() {
        assertThrows(IllegalArgumentException.class, () -> new ServerConfig(-1, 4, 8, 10));
        assertThrows(IllegalArgumentException.class, () -> new ServerConfig(65_536, 4, 8, 10));
        assertThrows(IllegalArgumentException.class, () -> new ServerConfig(8080, 0, 8, 10));
        assertThrows(IllegalArgumentException.class, () -> new ServerConfig(8080, 4, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new ServerConfig(8080, 4, 8, 0));
        assertThrows(IllegalArgumentException.class, () -> new ServerConfig(8080, 8, 4, 10));
    }
}
