package com.shifthackz.aisdv1.data.mocks

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.storage.db.persistent.entity.GenerationResultEntity
import java.util.Date

val mockGenerationResultEntity = GenerationResultEntity(
    id = 5598L,
    imageBase64 = "img",
    originalImageBase64 = "inp",
    createdAt = Date(0),
    generationType = AiGenerationResult.Type.IMAGE_TO_IMAGE.key,
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

val mockGenerationResultEntities = listOf(mockGenerationResultEntity)
