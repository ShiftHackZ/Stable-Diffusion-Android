package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Converts SDAI data with `withModelName`.
 *
 * @param modelName model name value consumed by the API.
 * @return Result produced by `withModelName`.
 * @author Dmitriy Moroz
 */
internal fun AiGenerationResult.withModelName(modelName: String): AiGenerationResult =
    copy(modelName = modelName.trim())

/**
 * Converts SDAI data with `withModelName`.
 *
 * @param modelName model name value consumed by the API.
 * @return Result produced by `withModelName`.
 * @author Dmitriy Moroz
 */
internal fun List<AiGenerationResult>.withModelName(modelName: String): List<AiGenerationResult> =
    map { result -> result.withModelName(modelName) }
