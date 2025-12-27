plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(project(":nvplib:core"))
    implementation(project(":nvplib:nfc-desktop-smartcardio"))

    implementation(libs.kotlin.stdlib)

    testImplementation(libs.junit)
}
