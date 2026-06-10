plugins {
    alias(libs.plugins.generic.kmp.library)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.auth"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.security.crypto)
        }
    }
}
