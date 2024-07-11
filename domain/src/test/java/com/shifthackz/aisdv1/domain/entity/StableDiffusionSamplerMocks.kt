package com.shifthackz.aisdv1.domain.entity

val mockStableDiffusionSampler = listOf(
    StableDiffusionSampler(
        name = "sampler_1",
        aliases = listOf("alias_1"),
        options = mapOf("option" to "value"),
    ),
    StableDiffusionSampler(
        name = "sampler_2",
        aliases = listOf("alias_2"),
        options = mapOf("option" to "value"),
    ),
)
