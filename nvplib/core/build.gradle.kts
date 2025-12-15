import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mavenPublish)
}

val nvplibVersion: String? by project
group = "net.cacheux.nvplib"
version = nvplibVersion ?: "unknown"

kotlin {
    jvmToolchain(libs.versions.nvplibJava.get().toInt())

    jvm()

    val xcfName = "bytonioKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                api(libs.bytonio.core)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.junit)
                implementation(project(":nvplib:testing"))
                implementation(project(":utils"))
            }
        }
    }
}

ksp {
    arg("bytonio.packageName", "net.cacheux.nvplib.generated")
    arg("bytonio.dataSizeFormat", "short")
}

dependencies {
    add("kspCommonMainMetadata", libs.bytonio.processor)
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "nvplib-core", version.toString())

    pom {
        name = "NVP Lib Core"
        description = "Core library to read data from Novopen insulin pens"
        url = "https://github.com/lcacheux/nov-open-reader"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "lcacheux"
                name = "Leo Cacheux"
                email = "leo@cacheux.net"
            }
        }
        scm {
            connection = "scm:git:https://github.com/lcacheux/nov-open-reader.git"
            developerConnection = "scm:git:ssh://github.com/lcacheux/nov-open-reader.git"
            url = "https://github.com/lcacheux/nov-open-reader"
        }
    }
}

afterEvaluate {
    tasks.withType(KotlinCompilationTask::class).configureEach {
        if (name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
    tasks.findByName("sourcesJar")?.dependsOn("kspCommonMainKotlinMetadata")
}

