pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "AI Stable Diffusion Client v1"

val modules = listOf(
        ":app",
        ":core:common",
        ":core:imageprocessing",
        ":core:localization",
        ":core:notification",
        ":core:ui",
        ":core:validation",
        ":data",
        ":demo",
        ":domain",
        ":feature:auth",
        ":feature:diffusion",
        ":feature:work",
        ":network",
        ":presentation",
        ":storage",
)

include(modules)
