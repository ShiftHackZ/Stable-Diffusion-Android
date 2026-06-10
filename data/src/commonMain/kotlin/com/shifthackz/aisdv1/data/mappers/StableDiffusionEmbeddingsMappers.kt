package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity

//region DOMAIN -> ENTITY
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<Embedding>.mapDomainToEntity(): List<StableDiffusionEmbeddingEntity> =
    map(Embedding::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun Embedding.mapDomainToEntity(): StableDiffusionEmbeddingEntity = with(this) {
    StableDiffusionEmbeddingEntity(
        id = keyword,
        keyword = keyword,
    )
}
//endregion

//region ENTITY -> DOMAIN
/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @return Result produced by `mapEntityToDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionEmbeddingEntity>.mapEntityToDomain(): List<Embedding> =
    map(StableDiffusionEmbeddingEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun StableDiffusionEmbeddingEntity.mapEntityToDomain(): Embedding = with(this) {
    Embedding(keyword)
}
//endregion
