package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.network.model.StableDiffusionHyperNetworkRaw
import com.shifthackz.aisdv1.network.model.StableDiffusionLoraRaw
import com.shifthackz.aisdv1.network.response.KtorSdEmbeddingsResponse

fun List<StableDiffusionLoraRaw>.mapKtorRawToLoraDomain(): List<LoRA> =
    map(StableDiffusionLoraRaw::mapKtorRawToLoraDomain)

fun StableDiffusionLoraRaw.mapKtorRawToLoraDomain(): LoRA =
    LoRA(
        name = name ?: "",
        alias = alias ?: "",
        path = path ?: "",
    )

fun KtorSdEmbeddingsResponse.mapKtorRawToEmbeddingDomain(): List<Embedding> =
    loadedKeys.map(::Embedding)

fun List<StableDiffusionHyperNetworkRaw>.mapKtorRawToHyperNetworkDomain(): List<StableDiffusionHyperNetwork> =
    map(StableDiffusionHyperNetworkRaw::mapKtorRawToHyperNetworkDomain)

fun StableDiffusionHyperNetworkRaw.mapKtorRawToHyperNetworkDomain(): StableDiffusionHyperNetwork =
    StableDiffusionHyperNetwork(
        name = name ?: "",
        path = path ?: "",
    )
