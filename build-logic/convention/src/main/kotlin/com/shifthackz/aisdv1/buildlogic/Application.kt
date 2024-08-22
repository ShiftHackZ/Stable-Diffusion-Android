package com.shifthackz.aisdv1.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project

internal fun Project.configureApplication(
    commonExtension: ApplicationExtension,
) {
    commonExtension.apply {
        configureKotlinAndroid(this)
        bundle.language.enableSplit = false
    }
}
