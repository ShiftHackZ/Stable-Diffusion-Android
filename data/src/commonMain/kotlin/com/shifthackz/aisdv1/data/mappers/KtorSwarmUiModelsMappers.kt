package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.auth.BasicHttpAuthorization
import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw
import com.shifthackz.aisdv1.network.response.KtorSwarmUiModelsResponse

/**
 * Converts SDAI data with `mapToBasicHttpAuthorization`.
 *
 * @author Dmitriy Moroz
 */
fun AuthorizationCredentials.mapToBasicHttpAuthorization(): BasicHttpAuthorization? = when (this) {
    is AuthorizationCredentials.HttpBasic -> BasicHttpAuthorization(login, password)
    AuthorizationCredentials.None -> null
}

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun KtorSwarmUiModelsResponse.mapKtorRawToCheckpointDomain(): List<SwarmUiModel> =
    files?.mapKtorRawToCheckpointDomain() ?: emptyList()

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun List<SwarmUiModelRaw>.mapKtorRawToCheckpointDomain(): List<SwarmUiModel> =
    map(SwarmUiModelRaw::mapKtorRawToCheckpointDomain)

/**
 * Converts SDAI data with `mapKtorRawToCheckpointDomain`.
 *
 * @return Result produced by `mapKtorRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun SwarmUiModelRaw.mapKtorRawToCheckpointDomain(): SwarmUiModel =
    SwarmUiModel(
        name = name ?: "",
        title = title ?: "",
        author = author ?: "",
    )

/**
 * Converts SDAI data with `mapKtorRawToLoraDomain`.
 *
 * @return Result produced by `mapKtorRawToLoraDomain`.
 * @author Dmitriy Moroz
 */
fun KtorSwarmUiModelsResponse.mapKtorRawToLoraDomain(): List<LoRA> =
    files?.mapKtorRawToLoraDomain() ?: emptyList()

/**
 * Converts SDAI data with `mapKtorRawToLoraDomain`.
 *
 * @return Result produced by `mapKtorRawToLoraDomain`.
 * @author Dmitriy Moroz
 */
fun List<SwarmUiModelRaw>.mapKtorRawToLoraDomain(): List<LoRA> =
    map(SwarmUiModelRaw::mapKtorRawToLoraDomain)

/**
 * Converts SDAI data with `mapKtorRawToLoraDomain`.
 *
 * @return Result produced by `mapKtorRawToLoraDomain`.
 * @author Dmitriy Moroz
 */
fun SwarmUiModelRaw.mapKtorRawToLoraDomain(): LoRA =
    LoRA(
        name = name ?: "",
        alias = title ?: "",
        path = "",
    )

/**
 * Converts SDAI data with `mapKtorRawToEmbeddingDomain`.
 *
 * @return Result produced by `mapKtorRawToEmbeddingDomain`.
 * @author Dmitriy Moroz
 */
fun KtorSwarmUiModelsResponse.mapKtorRawToEmbeddingDomain(): List<Embedding> =
    files?.mapKtorRawToEmbeddingDomain() ?: emptyList()

/**
 * Converts SDAI data with `mapKtorRawToEmbeddingDomain`.
 *
 * @return Result produced by `mapKtorRawToEmbeddingDomain`.
 * @author Dmitriy Moroz
 */
fun List<SwarmUiModelRaw>.mapKtorRawToEmbeddingDomain(): List<Embedding> =
    map(SwarmUiModelRaw::mapKtorRawToEmbeddingDomain)
        .filter { it.keyword.isNotBlank() }

/**
 * Converts SDAI data with `mapKtorRawToEmbeddingDomain`.
 *
 * @return Result produced by `mapKtorRawToEmbeddingDomain`.
 * @author Dmitriy Moroz
 */
fun SwarmUiModelRaw.mapKtorRawToEmbeddingDomain(): Embedding =
    Embedding(embeddingKeyword())

private fun SwarmUiModelRaw.embeddingKeyword(): String {
    val raw = title?.takeIf(String::isNotBlank) ?: name.orEmpty()
    val fileName = raw
        .substringAfterLast('/')
        .substringAfterLast('\\')
    return fileName
        .substringBeforeLast('.', fileName)
        .trim()
}
