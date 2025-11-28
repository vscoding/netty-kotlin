plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

group = "io.intellij.kt.netty.frp"

version = "1.0"

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
    api(project(":commons-dependencies"))

    api(libs.commons.io)
    api(libs.fastjson2)
    api(libs.kotlin.reflect)

}
