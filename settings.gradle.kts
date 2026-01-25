pluginManagement {
    val buildIn = System.getenv("BUILD_IN") ?: "LOCAL"
    repositories {
        if (buildIn == "GITHUB_ACTIONS") {
            mavenCentral()
        }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.develocity") version ("4.3")
}

rootProject.name = "netty-kotlin"

include("commons-dependencies")

include(":examples")

include("netty-client-reconnect:echo-server")
include("netty-client-reconnect:reconnect-client")

include("netty-socks-server")
include("netty-tcp-proxy")
include("netty-tcp-loadbalancer")
include("netty-tcp-server-test")

include("netty-spring-boot")

include("netty-frp-tcp:frp-tcp-commons")
include("netty-frp-tcp:netty-frp-tcp-client")
include("netty-frp-tcp:netty-frp-tcp-server")
