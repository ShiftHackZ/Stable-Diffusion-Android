plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.core.imageprocessing"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.rx.kotlin)
}
