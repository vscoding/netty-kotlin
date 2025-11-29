# Netty Server TCP Test

`netty-server-tcp-test` is a versatile TCP utility server built with **Kotlin** and **Netty**. It is designed to facilitate the testing, debugging, and verification of TCP network connections.

The server can operate in different modes, allowing it to either simply echo back received data or log incoming traffic for detailed inspection.

## Features

* **Echo Mode**: Acts as an echo server. It reads incoming data, logs it, and then sends the exact same data back to the client.
* **Log Mode**: Acts as a sink server. It reads incoming data and logs it in multiple formats (Hex, String, ASCII) without sending any response. This is useful for protocol analysis.
* **Connection Tracking**: Logs client connection and disconnection events along with the remote address.
* **Detailed Payload Inspection**: Provides utilities to visualize byte arrays in Hex, UTF-8 String, and ASCII formats.
* **Docker Support**: Includes a `Dockerfile` and `docker-compose.yml` for easy containerized deployment.

## Tech Stack

* **Language**: Kotlin 2.2.x
* **Framework**: Netty 4.1.x
* **Build Tool**: Gradle (Kotlin DSL)
* **Logging**: SLF4J with Logback
* **JSON**: FastJSON2 (Project dependency)

## Configuration

The server is configured via environment variables.

| Environment Variable | Default | Description                                               |
|:---------------------|:--------|:----------------------------------------------------------|
| `SERVER_PORT`        | `8080`  | The port on which the TCP server listens.                 |
| `TEST_TYPE`          | `log`   | The operating mode of the server. Options: `echo`, `log`. |

## Getting Started

### Prerequisites

* JDK 21
* Gradle

### Running Locally with Gradle

You can run the server directly using the Gradle Wrapper:

**Run as Echo Server:**

```bash
export SERVER_PORT=8080 export TEST_TYPE=echo ./gradlew :netty-server-tcp-test:run
```

**Run as Log Server:**

```bash
export SERVER_PORT=9090 export TEST_TYPE=log ./gradlew :netty-server-tcp-test:run
```

### Running with Docker Compose

The project includes a `docker-compose.yml` file for quick startup.

```text
docker-compose up -d
```

This will start the server container on port `8080` in `log` mode (as configured in the compose file).

### Building the Fat Jar

To build a standalone executable jar (including all dependencies):

```bash
./gradlew :netty-server-tcp-test:fatJar
```

The output jar will be located at: `build/libs/netty-server-tcp-test-1.0.0-SNAPSHOT-all.jar`

## Modules Structure

* **`io.intellij.kt.netty.server.test.TcpServer`**: The main entry point. Parses configuration and bootstraps the Netty server.
* **`io.intellij.kt.netty.server.test.handlers`**:
* `ConnectionHandler`: Manages connection lifecycle logging.
* `EchoHandler`: Implements the echo logic.
* `LogHandler`: Implements the logging-only logic.
* **`io.intellij.kt.netty.server.test.LogBytesUtils`**: Utility class for formatting byte arrays into human-readable strings (Hex, ASCII).
