@file:Suppress("unused_variable")

plugins {
    `kotlin-multiplatform-library`
    `maven-publish`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":socket"))
                implementation(kotlin("reflect"))
                implementation("com.squareup.moshi:moshi:1.14.0")
                implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
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