plugins {
    alias(libs.plugins.generic.library)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.shifthackz.aisdv1.storage"
    defaultConfig {
        ksp {
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.google.gson)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rx)
    ksp(libs.androidx.room.compiler)
}
