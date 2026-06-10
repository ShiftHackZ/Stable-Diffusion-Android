package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.network.model.StableDiffusionHyperNetworkRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import com.shifthackz.aisdv1.network.response.KtorSdEmbeddingsResponse

/**
 * Converts SDAI data with `mapKtorRawToLoraDomain`.
 *
 * @return Result produced by `mapKtorRawToLoraDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionLoraRaw>.mapKtorRawToLoraDomain(): List<LoRA> =
    map(StableDiffusionLoraRaw::mapKtorRawToLoraDomain)

/**
 * Converts SDAI data with `mapKtorRawToLoraDomain`.
 *
 * @return Result produced by `mapKtorRawToLoraDomain`.
 * @author Dmitriy Moroz
 */
fun StableDiffusionLoraRaw.mapKtorRawToLoraDomain(): LoRA =
    LoRA(
        name = name ?: "",
        alias = alias ?: "",
        path = path ?: "",
    )

/**
 * Converts SDAI data with `mapKtorRawToEmbeddingDomain`.
 *
 * @return Result produced by `mapKtorRawToEmbeddingDomain`.
 * @author Dmitriy Moroz
 */
fun KtorSdEmbeddingsResponse.mapKtorRawToEmbeddingDomain(): List<Embedding> =
    loadedKeys.map(::Embedding)

/**
 * Converts SDAI data with `mapKtorRawToHyperNetworkDomain`.
 *
 * @return Result produced by `mapKtorRawToHyperNetworkDomain`.
 * @author Dmitriy Moroz
 */
fun List<StableDiffusionHyperNetworkRaw>.mapKtorRawToHyperNetworkDomain(): List<StableDiffusionHyperNetwork> =
    map(StableDiffusionHyperNetworkRaw::mapKtorRawToHyperNetworkDomain)

/**
 * Converts SDAI data with `mapKtorRawToHyperNetworkDomain`.
 *
 * @return Result produced by `mapKtorRawToHyperNetworkDomain`.
 * @author Dmitriy Moroz
 */
fun StableDiffusionHyperNetworkRaw.mapKtorRawToHyperNetworkDomain(): StableDiffusionHyperNetwork =
    StableDiffusionHyperNetwork(
        name = name ?: "",
        path = path ?: "",
    )
