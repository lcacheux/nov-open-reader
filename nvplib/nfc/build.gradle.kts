plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
}

val nvplibVersion: String? by project
group = "net.cacheux.nvplib"
version = nvplibVersion ?: "unknown"

kotlin {
    jvmToolchain(libs.versions.nvplibJava.get().toInt())
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
}

dependencies {
    implementation(project(":nvplib:core"))
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "nvplib-nfc-android", version.toString())

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
