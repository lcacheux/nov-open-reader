plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

group = "net.cacheux.nvp.ui"

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())

    jvm()
    androidTarget()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(compose.components.uiToolingPreview)
        }

        commonMain.dependencies {
            implementation(project(":model"))
            implementation(project(":utils"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.colorpicker.compose)
        }

        commonTest.dependencies {
            implementation(libs.junit)
        }
    }
}

compose.resources {
    publicResClass = true
}

android {
    namespace = "net.cacheux.nvp.ui"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    debugImplementation(libs.androidx.ui.tooling)
}
