# Netty Server TCP Test

`netty-server-tcp-test` is a versatile TCP utility server built on top of Netty and Kotlin. It serves as a testing and debugging tool for TCP network connections.

It supports different operating modes to either echo back data or simply log incoming traffic, making it ideal for verifying network connectivity, inspecting payloads, or benchmarking.

## Features

* **Echo Mode**: Acts as an echo server, sending any received data back to the client.
* **Log Mode**: Acts as a sink server, logging all received data (Hex, String, ASCII) without responding, useful for protocol inspection.
* **Connection Logging**: Tracks and logs client connection and disconnection events with remote addresses.
* **Detailed Payload Inspection**: Provides utility methods to print byte arrays in Hex, String, and ASCII formats.

## Getting Started

### Prerequisites

* Java 21
* Gradle

### Configuration

The server behavior can be configured via system properties:

| Property | Default | Description                                               |
|:---------|:--------|:----------------------------------------------------------|
| `port`   | `8080`  | The port number the server will listen on.                |
| `type`   | `echo`  | The operating mode of the server. Options: `echo`, `log`. |
