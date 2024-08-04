package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import com.shifthackz.aisdv1.network.response.SdEmbeddingsResponse
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity

//region RAW -> DOMAIN
fun SdEmbeddingsResponse.mapRawToCheckpointDomain(): List<StableDiffusionEmbedding> =
    loaded?.keys?.map(::StableDiffusionEmbedding) ?: emptyList()

//endregion

//region DOMAIN -> ENTITY
fun List<StableDiffusionEmbedding>.mapDomainToEntity(): List<StableDiffusionEmbeddingEntity> =
    map(StableDiffusionEmbedding::mapDomainToEntity)

fun StableDiffusionEmbedding.mapDomainToEntity(): StableDiffusionEmbeddingEntity = with(this) {
    StableDiffusionEmbeddingEntity(
        id = keyword,
        keyword = keyword,
    )
}
//endregion

//region ENTITY -> DOMAIN
fun List<StableDiffusionEmbeddingEntity>.mapEntityToDomain(): List<StableDiffusionEmbedding> =
    map(StableDiffusionEmbeddingEntity::mapEntityToDomain)

fun StableDiffusionEmbeddingEntity.mapEntityToDomain(): StableDiffusionEmbedding = with(this) {
    StableDiffusionEmbedding(keyword)
}
//endregion
