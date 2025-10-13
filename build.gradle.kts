import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.sonarqube)
    signing
}

val localProperties by extra { Properties().apply {
    load(File("$rootDir/local.properties").inputStream())
} }

val gpgSigningAvailable = listOf("gpgSigningKey", "gpgSigningPass").map {
    localProperties.containsKey(it)
}.all { it }

val signingIfAvailable by extra {
    { publication: Publication ->
        if (gpgSigningAvailable) {
            extensions.create<SigningExtension>("sign${publication.name}").apply {
                sign(publication)
                useInMemoryPgpKeys(
                    localProperties["gpgSigningKey"] as String,
                    localProperties["gpgSigningPass"] as String
                )
            }
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "lcacheux_nov-open-reader")
        property("sonar.organization", "lcacheux")
    }
}
