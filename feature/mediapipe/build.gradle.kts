plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.generic.flavors)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.mediapipe"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain"))
    implementation(libs.koin.core)
    implementation(libs.rx.kotlin)
    fullImplementation(libs.google.mediapipe.image.generator)
    playstoreImplementation(libs.google.mediapipe.image.generator)
    testImplementation(libs.test.junit)
}
