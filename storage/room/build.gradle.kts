plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())

    jvm()
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":model"))
                implementation(project(":storage:storage-interface"))
                implementation(libs.androidx.room.common)
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.junit)
            }
        }
    }
}

android {
    namespace = "net.cacheux.nvp.storage.room"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
