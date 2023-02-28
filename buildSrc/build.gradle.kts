plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.8.0"))
    implementation(kotlin("serialization", "1.8.0"))
    implementation("com.android.tools.build:gradle:7.4.0-beta02")
}
