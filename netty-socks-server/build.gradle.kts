plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "io.intellij.kt.netty.server"
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
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform() {
        includeEngines("junit-jupiter")
    }
}

tasks.register<Jar>("fatJar") {
    group = "build"

    archiveClassifier.set("all")

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    manifest {
        // attributes["Main-Class"] = "io.intellij.kt.netty.server.socks.SocksServerKt"
        attributes["Main-Class"] = "io.intellij.kt.netty.server.socks.SocksServer"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.build {
    dependsOn(tasks.named("fatJar"))
}
