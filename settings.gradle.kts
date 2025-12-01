pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
    }
}

rootProject.name = "netty-kotlin"

include("commons-dependencies")
include("netty-client-reconnect:echo-server")
include("netty-client-reconnect:reconnect-client")

include("netty-server-socks")
include("netty-server-tcp-proxy")
include("netty-tcp-loadbalancer")
include("netty-server-tcp-test")

include("netty-spring-boot")

include("netty-frp-tcp:frp-tcp-commons")
include("netty-frp-tcp:netty-frp-tcp-client")
include("netty-frp-tcp:netty-frp-tcp-server")
