plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.core.notification"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:ui"))
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.androidx.core)
        }
    }
}
