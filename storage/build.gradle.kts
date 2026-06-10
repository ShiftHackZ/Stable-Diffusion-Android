plugins {
    alias(libs.plugins.generic.kmp.library)
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

ksp {
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.android)
            implementation(libs.androidx.room.runtime)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
