dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://www.jitpack.io")
        google()
        mavenCentral()
    }
}
rootProject.name = "kmnkt"
include(":examples:app")
include(":examples:app-java")
include(":examples:AliyunIotDemo")
include(":socket")
include(":aliyun-iot")
