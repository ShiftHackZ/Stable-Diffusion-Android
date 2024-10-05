plugins {
    alias(libs.plugins.generic.library)
}

android {
    namespace = "com.shifthackz.aisdv1.data"
    testOptions.unitTests.all { test ->
        test.jvmArgs(
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED"
        )
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:imageprocessing"))
    implementation(project(":domain"))
    implementation(project(":network"))
    implementation(project(":storage"))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.rx.kotlin)
    implementation(libs.google.gson)
    implementation(libs.shifthackz.preferences)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.mockito)
    testImplementation(libs.test.mockk)
}
