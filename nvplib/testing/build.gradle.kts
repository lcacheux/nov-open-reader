plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.nvplibJava.get().toInt())
}

dependencies {
    implementation(project(":nvplib:core"))
    implementation(project(":utils"))

    testImplementation(libs.junit)
}
