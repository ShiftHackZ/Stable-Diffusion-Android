package com.shifthackz.aisdv1.domain.entity

data class LocalAiModel(
    val id: String,
    val name: String,
    val size: String,
    val sources: List<String>,
    val downloaded: Boolean = false,
    val selected: Boolean = false,
) {
    companion object {
        val CUSTOM = LocalAiModel(
            id = "CUSTOM",
            name = "Custom",
            size = "NaN",
            sources = emptyList(),
        )
    }
}
