@file:Suppress("UNUSED_VARIABLE")

package com.shifthackz.aisdv1.buildlogic

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

private val jacocoExclusions = listOf(
    "**/R.class",
    "**/R\$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
)

internal fun Project.jacocoCodeCoverageReporting(
    commonExtension: BaseExtension,
) {
    val buildTypes = commonExtension.buildTypes.map { it.name }
    val productFlavors = commonExtension
        .productFlavors
        .map { it.name }
        .takeIf(List<String>::isNotEmpty) ?: listOf("")

    productFlavors.forEach { productFlavorName ->
        buildTypes.forEach { buildTypeName ->
            val (sourceName, sourcePath) = if (productFlavorName.isEmpty()) {
                buildTypeName to buildTypeName
            } else {
                val type = buildTypeName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else "$it"
                }
                Pair(
                    "${productFlavorName}${type}",
                    "${productFlavorName}/${buildTypeName}"
                )
            }

            val testTaskName = buildString {
                append("test")
                append(
                    sourceName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else "$it"
                    }
                )
                append("UnitTest")
            }
            val jacocoTaskName = buildString {
                append("jacoco")
                append(
                    testTaskName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else "$it"
                    }
                )
                append("Coverage")
            }
            println("[Jacoco] Task -> ${project.displayName.replace("\'", "")}:${jacocoTaskName}")

            tasks.register(jacocoTaskName, JacocoReport::class.java) {
                dependsOn(testTaskName)
                group = "Reporting"
                description = buildString {
                    append("Generate Jacoco coverage reports on the ")
                    append(
                        sourceName.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else "$it"
                        }
                    )
                    append(" build")
                }

                reports {
                    xml.required.set(false)
                    csv.required.set(false)
                    html.required.set(true)
                }

                sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))

                classDirectories.setFrom(
                    files(
                        fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
                            exclude(jacocoExclusions)
                        },
                        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
                            exclude(jacocoExclusions)
                        },
                    )
                )

                executionData.setFrom(
                    files(
                        fileTree(layout.buildDirectory) {
                            include("**/*.exec", "**/*.ec")
                        }
                    )
                )
            }
        }
    }
}
