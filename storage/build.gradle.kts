plugins {
    alias(libs.plugins.generic.library)
    kotlin("kapt")
}

android {
    namespace = "com.shifthackz.aisdv1.storage"
    defaultConfig {
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", "true")
                arg("room.expandProjection", "true")
            }
        }
    }
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.google.gson)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rx)
    //ToDo migrate to KSP
    kapt(libs.androidx.room.compiler)
}
