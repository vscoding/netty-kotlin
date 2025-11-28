# Netty Kotlin

A multi-module repository showcasing production-style Netty applications written in Kotlin. It includes examples for connection management, SOCKS proxy, TCP proxying, and TCP load balancing with pluggable strategies.

## Requirements

- JDK 21
- Gradle Wrapper (included)
- Kotlin 2.2.x
- Netty 4.1.x

## Modules

- commons-dependencies  
  Shared utilities and centralized dependency management for all modules.

- netty-client-reconnect
  - echo-server: Minimal echo server used for local verification.
  - reconnect-client: Client demonstrating reconnect logic and initializer/pipeline setup.

- netty-server-socks  
  A SOCKS4/5 server featuring optional username/password authentication and connect/relay handlers.

- netty-server-tcp-proxy  
  A TCP hex-dump proxy illustrating frontend/backend handlers and a clear channel pipeline.

- netty-tcp-loadbalancer  
  A TCP load balancer supporting multiple selection strategies (round-robin, random, least-connection) with simple test fixtures.