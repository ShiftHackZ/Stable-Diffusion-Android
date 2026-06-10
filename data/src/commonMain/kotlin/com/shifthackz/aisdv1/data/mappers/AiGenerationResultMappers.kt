package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity

//region DOMAIN --> ENTITY
fun List<AiGenerationResult>.mapDomainToEntity(): List<GenerationResultEntity> =
    map(AiGenerationResult::mapDomainToEntity)

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
fun List<GenerationResultEntity>.mapEntityToDomain(): List<AiGenerationResult> =
    map(GenerationResultEntity::mapEntityToDomain)

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
