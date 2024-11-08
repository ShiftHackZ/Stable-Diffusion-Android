plugins {
    alias(libs.plugins.generic.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.shifthackz.aisdv1.presentation"
    testOptions.unitTests{
        all { test ->
            test.jvmArgs(
                "--add-opens", "java.base/java.lang=ALL-UNNAMED",
                "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED"
            )
        }
        isReturnDefaultValues = true
        isIncludeAndroidResources = true
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:imageprocessing"))
    implementation(project(":core:localization"))
    implementation(project(":core:notification"))
    implementation(project(":core:validation"))
    implementation(project(":domain"))

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.paging.rx)
    implementation(libs.androidx.exif)

    implementation(libs.google.material)
    implementation(libs.apache.stringutils)

    implementation(libs.rx.java)
    implementation(libs.rx.kotlin)
    implementation(libs.rx.android)

    implementation(libs.shifthackz.daynightswitch)
    implementation(libs.shifthackz.catppuccin.compose)
    implementation(libs.shifthackz.catppuccin.splash)
    implementation(libs.compose.gestures)
    implementation(libs.compose.crop)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.koin)
    testImplementation(libs.test.koin.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.turbine)
    testImplementation(libs.test.roboelectric)
    testImplementation(libs.test.compose.junit)
    debugImplementation(libs.test.compose.manifest)
}
