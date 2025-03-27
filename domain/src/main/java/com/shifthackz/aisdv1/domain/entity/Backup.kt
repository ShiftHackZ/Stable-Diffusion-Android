package com.shifthackz.aisdv1.domain.entity

import java.util.Date

data class Backup(
    val generatedAt: Date = Date(),
    val appVersion: String = "",
    val appConfiguration: Map<String, Any> = mapOf(),
    val gallery: List<AiGenerationResult> = emptyList(),
)
