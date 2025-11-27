plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "io.intellij.netty.server"
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
    implementation(project(":commons-dependencies"))

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform() {
        includeEngines("junit-jupiter")
    }
}
