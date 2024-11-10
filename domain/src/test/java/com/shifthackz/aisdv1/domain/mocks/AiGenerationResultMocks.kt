package com.shifthackz.aisdv1.domain.mocks

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import java.util.Date

val mockAiGenerationResult = AiGenerationResult(
    id = 5598L,
    image = "img",
    inputImage = "inp",
    createdAt = Date(),
    type = AiGenerationResult.Type.IMAGE_TO_IMAGE,
    prompt = "prompt",
    negativePrompt = "negative",
    width = 512,
    height = 512,
    samplingSteps = 7,
    cfgScale = 0.7f,
    restoreFaces = true,
    sampler = "sampler",
    seed = "5598",
    subSeed = "1504",
    subSeedStrength = 5598f,
    denoisingStrength = 1504f,
    hidden = false,
)

val mockAiGenerationResults = listOf(mockAiGenerationResult)
