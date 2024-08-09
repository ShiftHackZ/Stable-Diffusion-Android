package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.extensions.mapToUi
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.R as PresentationR

sealed interface GalleryDetailState : MviState {
    val tabs: List<Tab>
    val selectedTab: Tab
    val screenModal: Modal

    @Immutable
    data class Loading(
        override val tabs: List<Tab> = emptyList(),
        override val selectedTab: Tab = Tab.IMAGE,
        override val screenModal: Modal = Modal.None,
    ) : GalleryDetailState

    @Immutable
    data class Content(
        override val tabs: List<Tab> = emptyList(),
        override val selectedTab: Tab = Tab.IMAGE,
        override val screenModal: Modal = Modal.None,
        val generationType: AiGenerationResult.Type,
        val id: Long,
        val bitmap: Bitmap,
        val inputBitmap: Bitmap?,
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
        val subSeed: UiText,
        val subSeedStrength: UiText,
        val denoisingStrength: UiText,
    ) : GalleryDetailState

    fun withTab(tab: Tab): GalleryDetailState = when (this) {
        is Content -> copy(selectedTab = tab)
        is Loading -> copy(selectedTab = tab)
    }

    fun withDialog(dialog: Modal) = when (this) {
        is Content -> copy(screenModal = dialog)
        is Loading -> copy(screenModal = dialog)
    }

    enum class Tab(
        @StringRes val label: Int,
        @DrawableRes val iconRes: Int,
    ) {
        IMAGE(LocalizationR.string.gallery_tab_image, PresentationR.drawable.ic_image),
        ORIGINAL(LocalizationR.string.gallery_tab_original, PresentationR.drawable.ic_image),
        INFO(LocalizationR.string.gallery_tab_info, PresentationR.drawable.ic_text);

        companion object {
            fun consume(type: AiGenerationResult.Type): List<Tab> = when (type) {
                AiGenerationResult.Type.TEXT_TO_IMAGE -> listOf(
                    IMAGE, INFO,
                )

                AiGenerationResult.Type.IMAGE_TO_IMAGE -> entries
            }
        }
    }
}

fun Triple<AiGenerationResult, Base64ToBitmapConverter.Output, Base64ToBitmapConverter.Output?>.mapToUi(): GalleryDetailState.Content =
    let { (ai, out, original) ->
        GalleryDetailState.Content(
            tabs = GalleryDetailState.Tab.consume(ai.type),
            generationType = ai.type,
            id = ai.id,
            bitmap = out.bitmap,
            inputBitmap = original?.bitmap,
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
            subSeed = ai.subSeed.asUiText(),
            subSeedStrength = ai.subSeedStrength.toString().asUiText(),
            denoisingStrength = ai.denoisingStrength.toString().asUiText(),
        )
    }
