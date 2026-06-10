plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.core.imageprocessing"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:common"))
            implementation(libs.koin.core)
            implementation(libs.koin.android)
        }
    }
}
