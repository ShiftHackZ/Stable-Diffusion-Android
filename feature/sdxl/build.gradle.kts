plugins {
    alias(libs.plugins.generic.kmp.library)
    alias(libs.plugins.generic.flavors)
}

val stableDiffusionCppOpenClEnabled = providers
    .gradleProperty("sdai.sdcpp.opencl")
    .map(String::toBoolean)
    .getOrElse(true)

android {
    namespace = "com.shifthackz.aisdv1.feature.sdxl"

    defaultConfig {
        ndk {
            abiFilters += "arm64-v8a"
        }

        externalNativeBuild {
            cmake {
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DGGML_OPENMP=OFF",
                    "-DSDAI_SDCPP_ENABLE_OPENCL=$stableDiffusionCppOpenClEnabled",
                    "-DSDAI_SDCPP_ENABLE_VULKAN=ON",
                )
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
