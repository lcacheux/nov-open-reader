pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NVP Lib"
include(":app")
include(":nvplib:core")
include(":nvplib:nfc")
include(":logging")
include(":utils")
include(":nvplib:testing")
include(":storage:room")
include(":storage:storage-interface")
include(":ui")
include(":model")
