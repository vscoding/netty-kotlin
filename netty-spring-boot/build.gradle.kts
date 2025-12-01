plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
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

val nettyVersion = libs.versions.netty.all.get()

configurations {
    all {
        resolutionStrategy.eachDependency {
            if (requested.group == "io.netty") {
                useVersion(nettyVersion)
            }
        }
    }
}

dependencies {
    implementation(project(":commons-dependencies"))

    implementation(libs.kotlin.reflect)

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
