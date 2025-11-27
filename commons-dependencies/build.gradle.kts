plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

val projectJdkVersion = libs.versions.java.get().toInt()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(projectJdkVersion)
    }
}

kotlin {
    jvmToolchain(projectJdkVersion)
}

dependencies {
    api(libs.commons.lang3)
    api(libs.netty.all)
    api(libs.slf4j.api)
    api(libs.logback.classic)
    api(libs.logback.core)
}

tasks.jar {
    archiveFileName = "netty-kotlin-commons.jar"
}
