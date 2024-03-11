package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.network.model.StabilityTextPromptRaw

fun String.mapToStabilityPrompt(defaultWeight: Double = 1.0): List<StabilityTextPromptRaw> =
    buildList {
        this@mapToStabilityPrompt
            .split(',')
            .map(String::trim)
            .filter(String::isNotBlank)
            .map {
                if (it.startsWith("(") && it.endsWith(")") && it.split(":").size == 2) {
                    val value = it.replace("(", "").replace(")", "").split(":")
                    add(
                        StabilityTextPromptRaw(
                            text = value.firstOrNull() ?: "",
                            weight = value.lastOrNull()?.toDoubleOrNull() ?: defaultWeight,
                        )
                    )
                } else {
                    add(StabilityTextPromptRaw(it, defaultWeight))
                }
            }
    }
