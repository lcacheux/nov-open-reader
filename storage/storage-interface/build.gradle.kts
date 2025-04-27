plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":model"))

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
