plugins {
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
    implementation(project(":commons-dependencies"))

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform(){
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
        attributes["Main-Class"] = "io.intellij.kt.netty.server.tcpproxy.HexDumpProxyKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.build {
    dependsOn(tasks.named("fatJar"))
}
