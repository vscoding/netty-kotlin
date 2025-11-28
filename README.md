# Netty Kotlin

A multi-module repository showcasing production-style Netty applications written in Kotlin. It includes examples for connection management, SOCKS proxy, TCP proxying, TCP load balancing with pluggable strategies, and an FRP-like TCP tunneling demo (client/server).

## Requirements

- JDK 21
- Gradle Wrapper (included)
- Kotlin 2.2.x
- Netty 4.1.x

## Modules

- commons-dependencies  
  Shared utilities and centralized dependency/dependency-version management for all modules.

- netty-client-reconnect
  - echo-server: Minimal echo server used for local verification.
  - reconnect-client: Client demonstrating reconnect logic and initializer/pipeline setup.

- netty-server-socks  
  A SOCKS4/5 server featuring optional username/password authentication and connect/relay handlers.

- netty-server-tcp-proxy  
  A TCP hex-dump proxy illustrating frontend/backend handlers and a clear channel pipeline.

- netty-tcp-loadbalancer  
  A TCP load balancer supporting multiple selection strategies (round-robin, random, least-connection) with simple test fixtures.

- netty-frp-tcp  
  FRP-like TCP tunneling:
  - frp-tcp-commons: Shared protocol and codecs (FrpMsgType, FrpBasicMsg, encoders/decoders, heartbeat).
  - netty-frp-tcp-server: Multi-port server that accepts user connections, handshake/auth, dispatch management.
  - netty-frp-tcp-client: Client that connects to the server, establishes service channels, and forwards traffic.

## Quick Start

- Build all:
  - Unix/macOS: `./gradlew build`
  - Windows: `gradlew.bat build`

- Run FRP server:
  - `:netty-frp-tcp:netty-frp-tcp-server:run` (or run `FrpServerMain` from IDE)
  - Configs under `netty-frp-tcp/frp-tcp-commons/src/main/resources/`:
    - `server-config.json`
    - `client-config.json`
    - SSL assets under `ssl/` (optional)

- Run FRP client:
  - `:netty-frp-tcp:netty-frp-tcp-client:run` (or run `FrpClientMain`)

## Notes

- Kotlin/JVM toolchain set to Java 21.
- Fastjson2 is used for protocol JSON; when working with Kotlin data classes, add `fastjson2-kotlin` and `kotlin-reflect` if needed.
- SSL support is optional for FRP; see `SslContextUtils` and `ssl.sh`.
