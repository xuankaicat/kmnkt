@file:Suppress("UNUSED_VARIABLE")

plugins {
    `kotlin-multiplatform-library`
    `maven-publish`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":socket"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(components.getByName("release"))
            }
        }
    }
}