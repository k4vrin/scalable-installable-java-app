# Java HTTP server learning roadmap

Use the same small HTTP contract to compare server behavior, configuration, packaging, and lifecycle. Each milestone requires observable evidence; reading or a successful build alone does not prove the behavior.

| Stage | Implementation | Learning focus | Status |
| --- | --- | --- | --- |
| 1a | JDK `HttpServer` with its default executor | Establish and measure the baseline request-execution behavior | In progress |
| 1b | JDK `HttpServer` with a platform-thread pool | Add bounded concurrency and measure its behavior against the baseline | Planned |
| 1c | JDK `HttpServer` with a virtual-thread-per-request executor | Use Java 21 virtual threads and compare throughput, latency, and resource limits | Planned |
| 2 | Embedded Tomcat without Spring | Servlet request/response handling, explicit container setup and lifecycle, standalone packaging | Planned |
| 3 | Embedded Jetty without Spring | Explicit embedded-server setup and lifecycle; compare the same servlet contract with Tomcat | Planned |
| 4a | Spring Boot with Tomcat | Compare framework-provided setup with the earlier explicit configuration | Planned |
| 4b | Spring Boot with Jetty | Compare both Boot container variants against the same behavioral checks | Planned |

## JDK HTTP server milestones

Complete all three Java 21 `HttpServer` executor stages before adding another server:

1. Diagnose the unchanged starter and separate observed behavior from inferred causes.
2. Record the default executor's behavior as the concurrency baseline.
3. Add a bounded platform-thread pool and explicit lifecycle management.
4. Add a virtual-thread-per-request executor using Java 21.
5. Compare the three executors with the same deterministic workload and resource limits.
6. Make Maven execution and runtime configuration portable.
7. Build a reproducible container and verify it with an actual HTTP request.
8. Repair a materially changed case independently and review the evidence afterward.

## Comparison contract

Keep `GET /hello` returning status 200 and the exact body `Hello, World!` across implementations. For each stage, record:

- Selected Java, build-tool, and server versions; a clean build and direct launch.
- Default, supplied, and invalid runtime configuration behavior.
- A deterministic slow-request scenario demonstrating overlap and finite resource bounds.
- Overload behavior and process shutdown evidence.
- A container build from a clean context, followed by an HTTP probe.
- Which behavior is configured explicitly and which behavior the framework supplies.

These checks define learning acceptance criteria. Single-instance request concurrency is only one part of scalability.

## Preparing later stages

Before starting a new implementation, select supported server or framework versions compatible with Java 21 and verify them against official documentation. Choose the module layout and dependency versions when that stage begins so the comparison reflects maintained releases.
