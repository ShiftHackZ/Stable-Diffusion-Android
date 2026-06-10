plugins {
    alias(libs.plugins.generic.kmp.library)
}

android {
    namespace = "com.shifthackz.aisdv1.core.common"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.koin.core)
                implementation(libs.timber)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.test.junit)
            }
        }
    }
}
