plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://repo.eclipse.org/content/repositories/paho-snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.6.10"))
    implementation(kotlin("serialization", "1.6.10"))
    implementation("com.android.tools.build:gradle:7.0.4")
}
