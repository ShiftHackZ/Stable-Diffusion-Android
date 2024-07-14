package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload

val mockImageToImagePayload = ImageToImagePayload(
    base64Image = "",
    base64MaskImage = "",
    denoisingStrength = 7f,
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
    inPaintingMaskInvert = 0,
    inPaintFullResPadding = 0,
    inPaintingFill = 0,
    inPaintFullRes = false,
    maskBlur = 0,
    stabilityAiClipGuidance = null,
    stabilityAiStylePreset = null,
)
