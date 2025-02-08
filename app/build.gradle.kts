import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.compose)
}

group = "net.cacheux.nvp.app"

kotlin {
    jvm("desktop")

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(project(":nvplib:nfc"))

            implementation(libs.androidx.activity.compose)

            implementation(libs.hilt.android)

            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences)

            configurations["kspAndroid"].dependencies.add(project.dependencies.create(libs.hilt.android.compiler.get()))
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.espresso.core)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.hilt.android.testing)
                implementation(libs.mockito.android)
                implementation(libs.mockito.kotlin)
                implementation(libs.turbine)
                implementation(compose.desktop.uiTestJUnit4)
                configurations["ksp"].dependencies.add(project.dependencies.create(libs.hilt.android.compiler.get()))
            }
        }

        commonMain.dependencies {
            implementation(libs.core.ktx)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(project(":nvplib:core"))
            implementation(project(":storage:storage-interface"))
            implementation(project(":storage:room"))

            implementation(project(":utils"))
            implementation(project(":logging"))
            implementation(project(":model"))
            implementation(project(":ui"))

            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.androidx.room.common)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        val desktopMain by getting {
            dependencies {
                implementation(project(":nvplib:testing"))
                implementation(compose.desktop.currentOs)
                implementation(libs.filekit.compose)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockito.kotlin)
            }
        }
    }
}

val localProperties : Properties by project
val signingAvailable = listOf("keystore.path", "keystore.pass", "keystore.key", "keystore.keyPass").map {
    localProperties.containsKey(it)
}.all { it }

android {
    namespace = "net.cacheux.nvp.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.cacheux.nvp.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.compileSdk.get().toInt()
        versionCode = 10102
        versionName = "1.1.2"

        testInstrumentationRunner = "net.cacheux.nvp.app.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    if (signingAvailable) {
        signingConfigs {
            create("release") {
                storeFile = File(localProperties["keystore.path"] as String)
                storePassword = localProperties["keystore.pass"] as String
                keyAlias = localProperties["keystore.key"] as String
                keyPassword = localProperties["keystore.keyPass"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (signingAvailable) signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "net.cacheux.nvp.app"
            packageVersion = "1.0.0"
        }
    }
}
