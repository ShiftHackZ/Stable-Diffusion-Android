package com.shifthackz.aisdv1.presentation.model

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion

sealed interface Modal {
    data object None : Modal
    data object LoadingRandomImage : Modal
    data class Generating(val status: LocalDiffusion.Status? = null) : Modal {
        val pair: Pair<Int, Int>?
            get() = status?.let { (current, total) -> current to total }
    }
    data class Communicating(val hordeProcessStatus: HordeProcessStatus? = null) : Modal
    data object PromptBottomSheet : Modal
    data class ExtraBottomSheet(
        val prompt: String,
        val type: ExtraType,
    ) : Modal
    sealed interface Image : Modal {
        data class Single(val result: AiGenerationResult, val autoSaveEnabled: Boolean): Image
        data class Batch(val results: List<AiGenerationResult>, val autoSaveEnabled: Boolean): Image

        companion object {
            fun create(list: List<AiGenerationResult>, autoSaveEnabled: Boolean): Image =
                if (list.size > 1) Batch(list, autoSaveEnabled)
                else Single(list.first(), autoSaveEnabled)
        }
    }
    data class Error(val error: UiText) : Modal
}
