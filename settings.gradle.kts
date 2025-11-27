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
