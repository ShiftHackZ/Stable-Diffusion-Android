package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity

//region DOMAIN --> ENTITY
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<AiGenerationResult>.mapDomainToEntity(): List<GenerationResultEntity> =
    map(AiGenerationResult::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun AiGenerationResult.mapDomainToEntity(): GenerationResultEntity = with(this) {
    GenerationResultEntity(
        id = id,
        imageBase64 = image,
        originalImageBase64 = inputImage,
        createdAt = createdAt,
        generationType = type.key,
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        sampler = sampler,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength,
        denoisingStrength = denoisingStrength,
        hidden = hidden,
    )
}
//endregion

//region ENTITY --> DOMAIN
/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<GenerationResultEntity>.mapEntityToDomain(): List<AiGenerationResult> =
    map(GenerationResultEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun GenerationResultEntity.mapEntityToDomain(): AiGenerationResult = with(this) {
    AiGenerationResult(
        id = id,
        image = imageBase64,
        inputImage = originalImageBase64,
        createdAt = createdAt,
        type = AiGenerationResult.Type.parse(generationType),
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        sampler = sampler,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength,
        denoisingStrength = denoisingStrength,
        hidden = hidden,
    )
}
//endregion
