package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw

/**
 * Converts SDAI data with `mapRawToCheckpointDomain`.
 *
 * @return Result produced by `mapRawToCheckpointDomain`.
 * @author Dmitriy Moroz
 */
fun List<HuggingFaceModelRaw>.mapRawToCheckpointDomain(): List<HuggingFaceModel> =
    mapNotNull(HuggingFaceModelRaw::mapRawToCheckpointDomain)

/**
 * Converts SDAI data with `mapRawToCheckpointDomain`.
 *
 * @author Dmitriy Moroz
 */
fun HuggingFaceModelRaw.mapRawToCheckpointDomain(): HuggingFaceModel? = with(this) {
    val modelAlias = alias ?: id ?: return@with null
    HuggingFaceModel(
        id = id ?: modelAlias,
        name = name ?: modelAlias.substringAfter('/'),
        alias = modelAlias,
        source = source ?: "https://huggingface.co/$modelAlias",
    )
}
