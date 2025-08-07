plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(project(":model"))

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
