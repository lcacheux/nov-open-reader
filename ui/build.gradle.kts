plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.android.library)
}

group = "net.cacheux.nvp.ui"

kotlin {
    jvmToolchain(17)

    jvm()
    androidTarget()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.components.uiToolingPreview)
        }

        commonMain.dependencies {
            implementation(project(":model"))
            implementation(project(":utils"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation("com.github.skydoves:colorpicker-compose:1.1.2")
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

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}
