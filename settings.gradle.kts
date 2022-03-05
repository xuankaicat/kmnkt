dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://www.jitpack.io")
        google()
        mavenCentral()
    }
}
rootProject.name = "communicate-multiplatform"
include(":examples:app")
include(":examples:app-java")
include(":examples:AliyunIotDemo")
include(":communicate")
include(":communicate-aliyun-iot")
