plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(project(":core:common"))
            implementation(libs.koin.core)
        }
        androidUnitTest.dependencies {
            implementation(libs.test.junit)
            implementation(libs.test.mockito)
            implementation(libs.test.mockk)
            implementation(libs.test.coroutines)
        }
    }
}
