plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.demo"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":domain"))
    implementation(libs.koin.core)
    implementation(libs.rx.kotlin)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
}
