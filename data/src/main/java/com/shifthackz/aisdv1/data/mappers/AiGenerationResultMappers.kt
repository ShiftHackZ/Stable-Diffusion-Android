package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.AiGenerationResultDomain
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import java.util.*

//region DOMAIN --> ENTITY
fun List<AiGenerationResultDomain>.mapDomainToEntity(): List<GenerationResultEntity> =
    map(AiGenerationResultDomain::mapDomainToEntity)

fun AiGenerationResultDomain.mapDomainToEntity(): GenerationResultEntity = with(this) {
    GenerationResultEntity(
        id = 0L,
        imageBase64 = image,
        cratedAt = Date()
    )
}
//endregion

//region ENTITY --> DOMAIN
fun List<GenerationResultEntity>.mapEntityToDomain(): List<AiGenerationResultDomain> =
    map(GenerationResultEntity::mapEntityToDomain)

fun GenerationResultEntity.mapEntityToDomain(): AiGenerationResultDomain = with(this) {
    AiGenerationResultDomain(
        image = imageBase64,
    )
}
//endregion
