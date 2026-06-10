package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse

fun AuthorizationCredentials.mapToBasicHttpAuthorization(): BasicHttpAuthorization? = when (this) {
    is AuthorizationCredentials.HttpBasic -> BasicHttpAuthorization(login, password)
    AuthorizationCredentials.None -> null
}

fun KtorSwarmUiModelsResponse.mapKtorRawToCheckpointDomain(): List<SwarmUiModel> =
    files?.mapKtorRawToCheckpointDomain() ?: emptyList()

fun List<SwarmUiModelRaw>.mapKtorRawToCheckpointDomain(): List<SwarmUiModel> =
    map(SwarmUiModelRaw::mapKtorRawToCheckpointDomain)

fun SwarmUiModelRaw.mapKtorRawToCheckpointDomain(): SwarmUiModel =
    SwarmUiModel(
        name = name ?: "",
        title = title ?: "",
        author = author ?: "",
    )

fun KtorSwarmUiModelsResponse.mapKtorRawToLoraDomain(): List<LoRA> =
    files?.mapKtorRawToLoraDomain() ?: emptyList()

fun List<SwarmUiModelRaw>.mapKtorRawToLoraDomain(): List<LoRA> =
    map(SwarmUiModelRaw::mapKtorRawToLoraDomain)

fun SwarmUiModelRaw.mapKtorRawToLoraDomain(): LoRA =
    LoRA(
        name = name ?: "",
        alias = title ?: "",
        path = "",
    )

fun KtorSwarmUiModelsResponse.mapKtorRawToEmbeddingDomain(): List<Embedding> =
    files?.mapKtorRawToEmbeddingDomain() ?: emptyList()

fun List<SwarmUiModelRaw>.mapKtorRawToEmbeddingDomain(): List<Embedding> =
    map(SwarmUiModelRaw::mapKtorRawToEmbeddingDomain)

fun SwarmUiModelRaw.mapKtorRawToEmbeddingDomain(): Embedding =
    Embedding(title ?: "")
