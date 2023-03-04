package com.shifthackz.aisdv1.domain.entity

data class StableDiffusionSamplerDomain(
    val name: String,
    val aliases: List<String>,
    val options: Map<String, String>,
)
