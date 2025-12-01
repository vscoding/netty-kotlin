# Netty Kotlin

A collection of high-performance network modules and utilities implemented in **Kotlin**, built on top of the **Netty** asynchronous event-driven network framework.

This repository contains several distinct modules for handling various TCP networking tasks, including proxies, load balancing, and client connectivity.

## Modules

The project is organized into several modules, each serving a specific networking purpose:

* **commons-dependencies**
  * Core module containing shared dependencies and utility classes (e.g., `ByteUtils`, `ChannelUtils`) used across other modules.

* **netty-client-reconnect**
  * `reconnect-client`: A robust TCP client implementation featuring automatic reconnection logic for resilient connectivity.
  * `echo-server`: A simple server used for testing the reconnection client.

* **netty-socks-server**
  * Implementation of a SOCKS proxy server.

* **netty-tcp-proxy**
  * A TCP proxy server designed to forward traffic between clients and backend servers.
  * Includes capabilities like `HexDumpProxy` for traffic inspection and debugging.

* **netty-tcp-loadbalancer**
  * A TCP load balancer module for distributing incoming network traffic across multiple backend servers.

* **netty-tcp-server-test**
  * A versatile TCP testing server (`TcpServer`) that supports multiple modes:
    * **Echo Mode**: Echoes received data back to the client.
    * **Log Mode**: Logs received bytes (Hex/ASCII) for analysis without responding.

* **netty-spring-boot**
  * Integration module for using Netty within Spring Boot applications.
  * Provides a `NettyServer` class that can be easily configured and started as part of a Spring Boot application.

* **netty-frp-tcp**
  * A Fast Reverse Proxy (FRP) implementation over TCP.
  * `netty-frp-tcp-client`: The client-side component for the reverse proxy.
  * `netty-frp-tcp-server`: The server-side component for the reverse proxy.
  * `frp-tcp-commons`: Shared definitions and logic for the FRP modules.

## Tech Stack

* **Language**: Kotlin
* **Network Framework**: Netty (4.1.x)
* **Build System**: Gradle (Kotlin DSL)
* **Logging**: SLF4J with Logback
* **Utilities**:
  * FastJSON2 (Configuration parsing)
  * Commons IO / Lang3

## Prerequisites

* **JDK**: Java 21
* **Kotlin**: 2.2.x
