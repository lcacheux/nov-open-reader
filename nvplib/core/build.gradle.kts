plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    signing
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

dependencies {
    implementation(kotlin("reflect"))

    testImplementation(libs.junit)
    testImplementation(project(":nvplib:testing"))
    testImplementation(project(":utils"))
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("release"))
        }
    }

    publications {
        create<MavenPublication>("mavenCore") {
            groupId = "net.cacheux.nvplib"
            artifactId = "nvplib-core"
            version = "0.1.1"
            afterEvaluate {
                from(components["java"])
                artifact(tasks["dokkaHtmlJar"])
                artifact(tasks["dokkaJavadocJar"])
            }

            pom {
                name = "NVP Lib Core"
                description = "Core library to read data from Novopen insulin pens"
                url = "https://github.com/lcacheux/nov-open-reader"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "lcacheux"
                        name = "Leo Cacheux"
                        email = "leo@cacheux.net"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/lcacheux/nov-open-reader.git"
                    developerConnection = "scm:git:ssh://github.com/lcacheux/nov-open-reader.git"
                    url = "https://github.com/lcacheux/nov-open-reader"
                }
            }
        }
    }
}

val signingIfAvailable: (Publication) -> Unit by project
signingIfAvailable(publishing.publications.getByName("mavenCore"))

tasks.create<Zip>("bundleZip") {
    dependsOn("publish")
    from(layout.buildDirectory.dir("release").get()) {
        exclude("**/*.asc.*")
    }
    archiveFileName = "nvplib-core.zip"
    destinationDirectory = layout.buildDirectory.dir("bundle").get()
}
