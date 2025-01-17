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

rootProject.name = "nov-open-reader"

include(":app")
include(":logging")
include(":model")
include(":utils")
include(":ui")
include(":nvplib:core")
include(":nvplib:nfc")
include(":nvplib:testing")
include(":storage:room")
include(":storage:storage-interface")
