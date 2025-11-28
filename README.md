# Netty Kotlin

A collection of high-performance network modules and utilities implemented in **Kotlin**, built on top of the **Netty** asynchronous event-driven network framework.

This repository contains several distinct modules for handling various TCP networking tasks, including proxies, load balancing, and client connectivity.

## Modules

The project is organized into several submodules:

### 1. netty-tcp-loadbalancer

A TCP Load Balancer implementation.

- **Key Features**: Handles incoming TCP traffic and distributes it to backend servers.
- **Configuration**: Uses `lb-config.json` for defining listening ports and backend targets.
- **Structure**: Includes logic for connection handling (`handlers`) and target selection strategies (`selector`).

### 2. netty-server-socks

A SOCKS proxy server implementation.

- Handle SOCKS protocol negotiation and traffic relaying.

### 3. netty-server-tcp-proxy

A standard TCP Proxy server.

- Forwards TCP traffic from a local port to a remote destination.

### 4. netty-frp-tcp

**FRP (Fast Reverse Proxy)** implementation for TCP.

- Likely used to expose a local server behind a NAT or firewall to the internet.

### 5. netty-client-reconnect

A client-side utility module.

- Focuses on robust TCP client connections with automatic reconnection logic.

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
