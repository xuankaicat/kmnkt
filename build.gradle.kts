allprojects {
    group = "com.gitee.xuankaicat.kmnkt"
    version = "2.0.0-alpha06"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.freeCompilerArgs += "-Xmulti-platform"
    }
}