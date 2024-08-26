package com.shifthackz.aisdv1.buildlogic

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

internal fun Project.configureFlavors(
    commonExtension: BaseExtension,
) {
    commonExtension.apply {
        flavorDimensions("type")
        productFlavors.create("full") {
            dimension = "type"
            applicationIdSuffix = ".full"
            resValue("string", "app_name", "SDAI Full")
            buildConfigField("String", "BUILD_FLAVOR_TYPE", "\"FULL\"")

        }
        productFlavors.create("foss") {
            dimension = "type"
            applicationIdSuffix = ".foss"
            resValue("string", "app_name", "SDAI FOSS")
            buildConfigField("String", "BUILD_FLAVOR_TYPE", "\"FOSS\"")

        }
        productFlavors.create("playstore") {
            dimension = "type"
            resValue("string", "app_name", "SDAI")
            buildConfigField("String", "BUILD_FLAVOR_TYPE", "\"GOOGLE_PLAY\"")
        }
    }
}

internal fun Project.configureFlavorsCommon(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        flavorDimensions += listOf("type")
        productFlavors.create("full") { dimension = "type" }
        productFlavors.create("foss") { dimension = "type" }
        productFlavors.create("playstore") { dimension = "type" }
    }
}
