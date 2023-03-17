import org.gradle.kotlin.dsl.*
import java.util.*

plugins {
    `maven-publish`
    signing
}

ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["ossrh.username"] = null
ext["ossrh.password"] = null

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
        println("set ${name} to $value")
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

fun getExtraString(name: String) = ext[name]?.toString()

publishing {
    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()

        pom {
            name.set("kmnkt")
            description.set("It is a library allow user easy to create a new connection like UDP, TCP, MQTT in Kotlin, support create a controller to manage MQTT connections, also support connect to Aliyun by Alink protocol.")
            url.set("https://gitee.com/xuankaicat/kmnkt")
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://gitee.com/xuankaicat/kmnkt/blob/master/LICENSE")
                }
            }
            developers {
                developer {
                    id.set("xuankaicat")
                    name.set("xuankaicat")
                    email.set("1277961681@qq.com")
                }
            }
            scm {
                connection.set("scm:git:git://gitee.com/xuankaicat/kmnkt.git")
                developerConnection.set("scm:git:ssh://gitee.com/xuankaicat/kmnkt.git")
                url.set("https://gitee.com/xuankaicat/kmnkt")
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            setUrl(if (project.version.toString().endsWith("SNAPSHOT")) {
                "https://s01.oss.sonatype.org/content/repositories/snapshots"
            } else {
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            })
            credentials {
                username = getExtraString("ossrh.username")
                password = getExtraString("ossrh.password")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}