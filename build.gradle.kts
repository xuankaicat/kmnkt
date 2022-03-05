allprojects {
    group = "com.gitee.xuankaicat"
    version = "2.0.0-alpha"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.freeCompilerArgs += "-Xmulti-platform"
    }
}