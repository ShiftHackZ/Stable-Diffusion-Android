plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.work"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:localization"))
    implementation(project(":core:notification"))
    implementation(project(":domain"))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.rx.kotlin)
    implementation(libs.androidx.work.runtime)
}
