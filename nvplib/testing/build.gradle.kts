plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":nvplib:core"))
    implementation(project(":utils"))

    testImplementation(libs.junit)
}
