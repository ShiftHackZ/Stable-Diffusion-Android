package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.network.response.DownloadableModelResponse

val mockDownloadableModelsResponse = listOf(
    DownloadableModelResponse(
        id = "1",
        name = "Model 1",
        size = "5 Gb",
        sources = listOf("https://example.com/1.html"),
    )
)
