package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.network.response.SdEmbeddingsResponse

val mockSdEmbeddingsResponse = SdEmbeddingsResponse(
    loaded = mapOf("1504" to "5598"),
)

val mockEmptySdEmbeddingsResponse = SdEmbeddingsResponse(
    loaded = emptyMap(),
)
