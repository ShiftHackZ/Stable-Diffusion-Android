pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "AI Stable Diffusion Client v1"

val publicModules = listOf(
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
    ":feature:benchmark",
    ":feature:bonsai",
    ":feature:coreml",
    ":feature:mediapipe",
    ":feature:onnx",
    ":feature:sdxl",
    ":feature:work",
    ":network",
    ":presentation",
    ":storage",
)

val optionalNonFreeModules = listOf(
    ":nonfree:admob",
    ":nonfree:iap",
    ":nonfree:localization",
    ":nonfree:sdai-cloud",
    ":nonfree:sdai-cloud-ui-kit",
).filter { module ->
    file(module.removePrefix(":").replace(':', '/') + "/build.gradle.kts").exists()
}

include(publicModules + optionalNonFreeModules)

project(":app").projectDir = file("app/android")
