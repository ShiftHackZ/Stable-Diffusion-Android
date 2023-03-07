package com.shifthackz.aisdv1.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shifthackz.aisdv1.network.response.SdGenerationResponse

fun mapSeedFromRemote(infoString: String): String {
    return try {
        val info = Gson().fromJson<SdGenerationResponse.Info>(
            infoString,
            object : TypeToken<SdGenerationResponse.Info>() {}.type
        )
        info.seed.toString()
    } catch (e: Exception) {
        ""
    }
}
