package com.shifthackz.aisdv1.domain.entity

sealed interface BackgroundWorkResult {
    data object None : BackgroundWorkResult
    data class Success(val ai: List<AiGenerationResult>) : BackgroundWorkResult
    data class Error(val t: Throwable) : BackgroundWorkResult
}
