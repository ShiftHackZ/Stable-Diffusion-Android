package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion

sealed interface Modal {
    data object None : Modal

    data object LoadingRandomImage : Modal

    @Immutable
    data class Generating(val status: LocalDiffusion.Status? = null) : Modal {
        val pair: Pair<Int, Int>?
            get() = status?.let { (current, total) -> current to total }
    }

    @Immutable
    data class Communicating(val hordeProcessStatus: HordeProcessStatus? = null) : Modal

    data object PromptBottomSheet : Modal

    @Immutable
    data class ExtraBottomSheet(
        val prompt: String,
        val negativePrompt: String,
        val type: ExtraType,
    ) : Modal

    @Immutable
    data class Embeddings(
        val prompt: String,
        val negativePrompt: String,
    ) : Modal

    sealed interface Image : Modal {

        @Immutable
        data class Single(val result: AiGenerationResult, val autoSaveEnabled: Boolean): Image

        @Immutable
        data class Batch(val results: List<AiGenerationResult>, val autoSaveEnabled: Boolean): Image

        companion object {
            fun create(list: List<AiGenerationResult>, autoSaveEnabled: Boolean): Image =
                if (list.size > 1) Batch(list, autoSaveEnabled)
                else Single(list.first(), autoSaveEnabled)
        }
    }

    @Immutable
    data class Error(val error: UiText) : Modal
}
