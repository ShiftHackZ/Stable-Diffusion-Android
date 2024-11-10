package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

val mockGalleryDetailTxt2Img = GalleryDetailState.Content(
    tabs = listOf(
        GalleryDetailState.Tab.IMAGE,
        GalleryDetailState.Tab.INFO
    ),
    selectedTab = GalleryDetailState.Tab.IMAGE,
    generationType = AiGenerationResult.Type.TEXT_TO_IMAGE,
    id = 0L,
    bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888),
    inputBitmap = null,
    cfgScale = "0.7".asUiText(),
    createdAt = "01.01.1970".asUiText(),
    type = "txt2img".asUiText(),
    prompt = "input".asUiText(),
    negativePrompt = "output".asUiText(),
    size = "512x512".asUiText(),
    samplingSteps = "12".asUiText(),
    restoreFaces = "false".asUiText(),
    sampler = "Euler A".asUiText(),
    seed = "0000".asUiText(),
    subSeed = "0001".asUiText(),
    subSeedStrength = "".asUiText(),
    denoisingStrength = "".asUiText(),
    hidden = false,
)
