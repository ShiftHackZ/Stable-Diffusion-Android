plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.koin.core)
    implementation(libs.rx.kotlin)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
    testImplementation(libs.test.mockk)
}
