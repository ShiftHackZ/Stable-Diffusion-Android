plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.diffusion"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain"))
    implementation(libs.koin.core)
    implementation(libs.rx.kotlin)
    implementation(libs.google.mediapipe.image.generator) {
        exclude(group = "com.google.firebase", module = "firebase-encoders")
        exclude(group = "com.google.firebase", module = "firebase-encoders-json")
        exclude(group = "com.google.firebase", module = "firebase-encoders-proto")
        exclude(group = "com.google.flogger", module = "flogger")
        exclude(group = "com.google.flogger", module = "flogger-system-backend")
        exclude(group = "com.google.android.datatransport", module = "transport-api")
        exclude(group = "com.google.android.datatransport", module = "transport-backend-cct")
        exclude(group = "com.google.android.datatransport", module = "transport-runtime")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}
