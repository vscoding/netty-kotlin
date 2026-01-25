subprojects {
    version = "1.0.0-SNAPSHOT"
    group = "io.intellij.kt.netty"

    val buildIn = System.getenv("BUILD_IN") ?: "LOCAL"

    repositories {
        if (buildIn == "GITHUB_ACTIONS") {
            mavenCentral()
        }
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
    }

}