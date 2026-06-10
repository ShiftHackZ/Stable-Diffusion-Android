plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.core.validation"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.koin.core)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.test.junit)
                implementation(libs.test.mockk)
            }
        }
    }
}
