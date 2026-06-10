package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.network.response.KtorSdEmbeddingsResponse

val mockSdEmbeddingsResponse = KtorSdEmbeddingsResponse.fromLoadedKeys(setOf("1504"))

val mockEmptySdEmbeddingsResponse = KtorSdEmbeddingsResponse.fromLoadedKeys(emptySet())

val mockKtorSdEmbeddingsResponse = KtorSdEmbeddingsResponse.fromLoadedKeys(setOf("1504"))

val mockEmptyKtorSdEmbeddingsResponse = KtorSdEmbeddingsResponse.fromLoadedKeys(emptySet())
