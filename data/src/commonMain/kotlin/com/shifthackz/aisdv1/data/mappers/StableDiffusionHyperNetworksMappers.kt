package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionHyperNetworkEntity

//region DOMAIN -> ENTITY
/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @return Result produced by `mapDomainToEntity`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionHyperNetwork>.mapDomainToEntity(): List<StableDiffusionHyperNetworkEntity> =
    map(StableDiffusionHyperNetwork::mapDomainToEntity)

/**
 * Converts SDAI data with `mapDomainToEntity`.
 *
 * @author Dmitriy Moroz
 */
fun StableDiffusionHyperNetwork.mapDomainToEntity(): StableDiffusionHyperNetworkEntity = with(this) {
    StableDiffusionHyperNetworkEntity(
        id = name,
        name = name,
        path = path,
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
fun List<StableDiffusionHyperNetworkEntity>.mapEntityToDomain(): List<StableDiffusionHyperNetwork> =
    map(StableDiffusionHyperNetworkEntity::mapEntityToDomain)

/**
 * Converts SDAI data with `mapEntityToDomain`.
 *
 * @author Dmitriy Moroz
 */
fun StableDiffusionHyperNetworkEntity.mapEntityToDomain(): StableDiffusionHyperNetwork = with(this) {
    StableDiffusionHyperNetwork(name, path)
}
//endregion
