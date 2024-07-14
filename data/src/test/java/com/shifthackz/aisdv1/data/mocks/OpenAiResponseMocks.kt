package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.network.model.OpenAiImageRaw
import com.shifthackz.aisdv1.network.response.OpenAiResponse

val mockSuccessOpenAiResponse = OpenAiResponse(
    created = System.currentTimeMillis(),
    data = listOf(
        OpenAiImageRaw(
            "base64",
            "https://openai.com",
            "prompt",
        ),
    ),
)

val mockBadOpenAiResponse = OpenAiResponse(
    created = System.currentTimeMillis(),
    data = emptyList(),
)
