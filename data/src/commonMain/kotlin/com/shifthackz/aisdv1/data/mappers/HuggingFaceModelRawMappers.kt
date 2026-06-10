package com.shifthackz.aisdv1.data.mappers

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.network.model.HuggingFaceModelRaw

fun List<HuggingFaceModelRaw>.mapRawToCheckpointDomain(): List<HuggingFaceModel> =
    mapNotNull(HuggingFaceModelRaw::mapRawToCheckpointDomain)

fun HuggingFaceModelRaw.mapRawToCheckpointDomain(): HuggingFaceModel? = with(this) {
    val modelAlias = alias ?: id ?: return@with null
    HuggingFaceModel(
        id = id ?: modelAlias,
        name = name ?: modelAlias.substringAfter('/'),
        alias = modelAlias,
        source = source ?: "https://huggingface.co/$modelAlias",
    )
}
