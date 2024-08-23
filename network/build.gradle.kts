plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":feature:auth"))
    implementation(libs.google.gson)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.adapter.rxjava3)
    api(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.koin.core)
    implementation(libs.rx.kotlin)
    implementation(libs.rx.network)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockk)
}
