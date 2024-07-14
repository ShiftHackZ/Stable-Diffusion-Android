package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

val mockTextToImagePayload = TextToImagePayload(
    prompt = "prompt",
    negativePrompt = "negative",
    samplingSteps = 12,
    cfgScale = 0.7f,
    width = 512,
    height = 512,
    restoreFaces = true,
    seed = "5598",
    subSeed = "1504",
    subSeedStrength = 5598f,
    sampler = "sampler",
    nsfw = true,
    batchCount = 1,
    quality = null,
    style = null,
    openAiModel = null,
    stabilityAiClipGuidance = null,
    stabilityAiStylePreset = null,
)
