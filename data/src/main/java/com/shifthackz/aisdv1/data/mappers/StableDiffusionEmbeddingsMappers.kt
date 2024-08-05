package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.network.response.SdEmbeddingsResponse
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity

//region RAW -> DOMAIN
fun SdEmbeddingsResponse.mapRawToCheckpointDomain(): List<Embedding> =
    loaded?.keys?.map(::Embedding) ?: emptyList()

//endregion

//region DOMAIN -> ENTITY
fun List<Embedding>.mapDomainToEntity(): List<StableDiffusionEmbeddingEntity> =
    map(Embedding::mapDomainToEntity)

fun Embedding.mapDomainToEntity(): StableDiffusionEmbeddingEntity = with(this) {
    StableDiffusionEmbeddingEntity(
        id = keyword,
        keyword = keyword,
    )
}
//endregion

//region ENTITY -> DOMAIN
fun List<StableDiffusionEmbeddingEntity>.mapEntityToDomain(): List<Embedding> =
    map(StableDiffusionEmbeddingEntity::mapEntityToDomain)

fun StableDiffusionEmbeddingEntity.mapEntityToDomain(): Embedding = with(this) {
    Embedding(keyword)
}
//endregion
