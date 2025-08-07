plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())

    jvm()
    androidTarget()

    sourceSets {
        jvmTest.dependencies {
            implementation(libs.junit)
        }
    }
}

android {
    namespace = "net.cacheux.nvp.model"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
