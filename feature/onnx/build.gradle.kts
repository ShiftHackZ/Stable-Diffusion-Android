plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.onnx"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":domain"))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.microsoft.onnx.runtime.android)
        }
    }
}
