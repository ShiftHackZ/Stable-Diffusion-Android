package com.shifthackz.aisdv1.domain.entity

/**
 * Defines the `BackgroundWorkResult` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface BackgroundWorkResult {
    /**
     * Provides the `None` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : BackgroundWorkResult
    /**
     * Carries `Success` data through the SDAI domain layer.
     *
     * @param ai ai value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Success(val ai: List<AiGenerationResult>) : BackgroundWorkResult
    /**
     * Carries `Error` data through the SDAI domain layer.
     *
     * @param t t value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Error(val t: Throwable) : BackgroundWorkResult
}
