plugins {
    alias(libs.plugins.generic.kmp.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.work"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:localization"))
            implementation(project(":core:notification"))
            implementation(project(":domain"))
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.work.runtime)
        }
    }
}
