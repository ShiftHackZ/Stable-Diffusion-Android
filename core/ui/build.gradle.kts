plugins {
    alias(libs.plugins.generic.kmp.compose)
}

android {
    namespace = "com.shifthackz.aisdv1.core.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.androidx.lifecycle.viewmodel.core)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(project(":core:common"))

            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.runtime)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.material.icons)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.activity)
            implementation(libs.androidx.compose.viewmodel)
            implementation(libs.androidx.compose.navigation)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.compose)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
}
