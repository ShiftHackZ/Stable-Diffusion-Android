package com.shifthackz.aisdv1.domain.entity

data class StableDiffusionModel(
    val title: String,
    val modelName: String,
    val hash: String,
    val sha256: String,
    val filename: String,
    val config: String,
)
