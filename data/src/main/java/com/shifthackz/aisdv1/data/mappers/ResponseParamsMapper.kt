package com.shifthackz.aisdv1.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.network.response.SdGenerationResponse

fun mapSeedFromRemote(infoString: String): String = parseInfo(infoString).fold(
    onFailure = { "" },
    onSuccess = { "${it.seed}" }
)

fun mapSubSeedFromRemote(infoString: String): String = parseInfo(infoString).fold(
    onFailure = { "" },
    onSuccess = { "${it.subSeed}" }
)

private fun parseInfo(infoString: String) = runCatching {
    return@runCatching Gson().fromJson<SdGenerationResponse.Info>(
        infoString,
        object : TypeToken<SdGenerationResponse.Info>() {}.type
    )
}.onFailure { errorLog("::parseInfo", it) }
