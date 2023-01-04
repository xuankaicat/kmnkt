@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://www.jitpack.io")
        google()
        mavenCentral()
    }
}
rootProject.name = "kmnkt"
include(":examples:app")
include(":examples:app-java")
include(":examples:AliyunIotDemo")
include(":examples:springbootDemo")
include(":socket")
include(":aliyun-iot")
include(":mqtt-enhance")
