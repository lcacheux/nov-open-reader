plugins {
    id("maven-publish")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    signing
}

android {
    namespace = "net.cacheux.nvplib.nfc"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":nvplib:core"))
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
        create<MavenPublication>("mavenNfc") {
            groupId = "net.cacheux.nvplib"
            artifactId = "nvplib-nfc"
            version = "0.1.0"
            afterEvaluate {
                from(components["release"])
                artifact(tasks["dokkaHtmlJar"])
                artifact(tasks["dokkaJavadocJar"])
            }

            pom {
                name = "NVP Lib NFC"
                description = "Android NFC implementation to read data from Novopen insulin pens"
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
signingIfAvailable(publishing.publications.getByName("mavenNfc"))

tasks.create<Zip>("bundleZip") {
    dependsOn("publish")
    from(layout.buildDirectory.dir("release").get()) {
        exclude("**/*.asc.*")
    }
    archiveFileName = "nvplib-nfc.zip"
    destinationDirectory = layout.buildDirectory.dir("bundle").get()
}
