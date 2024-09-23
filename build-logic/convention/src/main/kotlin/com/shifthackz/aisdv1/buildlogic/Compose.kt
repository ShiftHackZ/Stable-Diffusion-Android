package com.shifthackz.aisdv1.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
            buildConfig = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
        }

        dependencies {
            add("implementation", project(":core:common"))

            val bom = libs.findLibrary("androidx.compose.bom").get()
            add("implementation", platform(bom))


            add("implementation", libs.findLibrary("androidx.compose.runtime").get())
            add("implementation", libs.findLibrary("androidx.compose.material3").get())
            add("implementation", libs.findLibrary("androidx.compose.material.icons").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.graphics").get())
            add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            add("implementation", libs.findLibrary("androidx.compose.activity").get())
            add("implementation", libs.findLibrary("androidx.compose.viewmodel").get())
            add("implementation", libs.findLibrary("androidx.compose.navigation").get())
            add("implementation", libs.findLibrary("androidx.paging.runtime").get())
            add("implementation", libs.findLibrary("androidx.paging.compose").get())
            add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel").get())
            add("implementation", libs.findLibrary("androidx.lifecycle.compose").get())
            add("implementation", libs.findLibrary("rx.java").get())
            add("implementation", libs.findLibrary("shifthackz.mvi").get())
            add("implementation", libs.findLibrary("kotlinx.serialization.json").get())
        }
    }
}
