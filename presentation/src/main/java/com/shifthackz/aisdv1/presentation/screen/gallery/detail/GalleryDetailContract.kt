package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.extensions.mapToUi
import java.io.File

sealed interface GalleryDetailEffect : MviEffect {

    data class ShareImageFile(val file: File) : GalleryDetailEffect
}

sealed interface GalleryDetailState : MviState {

    val tab: Tab

    data class Loading(override val tab: Tab = Tab.IMAGE) : GalleryDetailState

    data class Content(
        override val tab: Tab = Tab.IMAGE,
        val id: Long,
        val bitmap: Bitmap,
        val createdAt: UiText,
        val type: UiText,
        val prompt: UiText,
        val negativePrompt: UiText,
        val size: UiText,
        val samplingSteps: UiText,
        val cfgScale: UiText,
        val restoreFaces: UiText,
        val sampler: UiText,
        val seed: UiText,
    ) : GalleryDetailState

    fun withTab(tab: Tab): GalleryDetailState = when (this) {
        is Content -> copy(tab = tab)
        is Loading -> Loading(tab)
    }

    enum class Tab {
        IMAGE,
        INFO;
    }
}

fun Pair<AiGenerationResult, Base64ToBitmapConverter.Output>.mapToUi(): GalleryDetailState.Content =
    let { (ai, out) ->
        GalleryDetailState.Content(
            id = ai.id,
            bitmap = out.bitmap,
            createdAt = ai.createdAt.toString().asUiText(),
            type = ai.type.key.asUiText(),
            prompt = ai.prompt.asUiText(),
            negativePrompt = ai.negativePrompt.asUiText(),
            size = "${ai.width} X ${ai.height}".asUiText(),
            samplingSteps = ai.samplingSteps.toString().asUiText(),
            cfgScale = ai.cfgScale.toString().asUiText(),
            restoreFaces = ai.restoreFaces.mapToUi(),
            sampler = ai.sampler.asUiText(),
            seed = ai.seed.asUiText(),
        )
    }
