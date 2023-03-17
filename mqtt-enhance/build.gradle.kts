@file:Suppress("unused_variable")

plugins {
    `kotlin-multiplatform-library`
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