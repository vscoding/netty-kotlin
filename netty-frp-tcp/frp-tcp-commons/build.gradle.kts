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

  testImplementation(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
  testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
  useJUnitPlatform() {
    includeEngines("junit-jupiter")
  }
}
