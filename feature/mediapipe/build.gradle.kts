plugins {
    alias(libs.plugins.generic.kmp.library)
    alias(libs.plugins.generic.flavors)
}

android {
    namespace = "com.shifthackz.aisdv1.feature.mediapipe"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":domain"))
            implementation(libs.koin.core)
        }

        androidUnitTest.dependencies {
            implementation(libs.test.junit)
        }
    }
}

dependencies {
    fullImplementation(libs.google.mediapipe.image.generator) {
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
    }
    fullImplementation(libs.google.protobuf.java)

    playstoreImplementation(libs.google.mediapipe.image.generator) {
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
    }
    playstoreImplementation(libs.google.protobuf.java)
}
