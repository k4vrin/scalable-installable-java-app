# Java HTTP server learning roadmap

Use the same small HTTP contract to compare server behavior, configuration, packaging, and lifecycle. Each milestone requires observable evidence; reading or a successful build alone does not prove the behavior.

| Stage | Implementation | Learning focus | Status |
| --- | --- | --- | --- |
| 1 | JDK `HttpServer` | Request execution, bounded concurrency, configuration, shutdown, Maven and Docker installability | In progress |
| 2 | Embedded Tomcat without Spring | Servlet request/response handling, explicit container setup and lifecycle, standalone packaging | Planned |
| 3 | Embedded Jetty without Spring | Explicit embedded-server setup and lifecycle; compare the same servlet contract with Tomcat | Planned |
| 4a | Spring Boot with Tomcat | Compare framework-provided setup with the earlier explicit configuration | Planned |
| 4b | Spring Boot with Jetty | Compare both Boot container variants against the same behavioral checks | Planned |

## JDK HTTP server milestones

Complete the initial implementation before adding another server:

1. Diagnose the unchanged starter and separate observed behavior from inferred causes.
2. Add bounded request concurrency and explicit lifecycle management.
3. Make Maven execution and runtime configuration portable.
4. Build a reproducible container and verify it with an actual HTTP request.
5. Repair a materially changed case independently and review the evidence afterward.

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
