package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity

//region DOMAIN --> ENTITY
fun List<AiGenerationResultDomain>.mapDomainToEntity(): List<GenerationResultEntity> =
    map(AiGenerationResultDomain::mapDomainToEntity)

fun AiGenerationResultDomain.mapDomainToEntity(): GenerationResultEntity = with(this) {
    GenerationResultEntity(
        id = id,
        imageBase64 = image,
        createdAt = createdAt,
        generationType = type.key,
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        seed = seed,
    )
}
//endregion

//region ENTITY --> DOMAIN
fun List<GenerationResultEntity>.mapEntityToDomain(): List<AiGenerationResultDomain> =
    map(GenerationResultEntity::mapEntityToDomain)

fun GenerationResultEntity.mapEntityToDomain(): AiGenerationResultDomain = with(this) {
    AiGenerationResultDomain(
        id = id,
        image = imageBase64,
        createdAt = createdAt,
        type = AiGenerationResultDomain.Type.parse(generationType),
        prompt = prompt,
        negativePrompt = negativePrompt,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        restoreFaces = restoreFaces,
        seed = seed,
    )
}
//endregion
