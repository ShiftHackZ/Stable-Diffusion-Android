plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.auth"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain"))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.androidx.security.crypto)
    implementation(libs.google.gson)
}
