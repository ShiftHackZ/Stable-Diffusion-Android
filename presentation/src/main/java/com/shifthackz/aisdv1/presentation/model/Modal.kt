package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState

sealed interface Modal {

    data object None : Modal

    data object LoadingRandomImage : Modal

    data object ClearAppCache : Modal

    data object DeleteImageConfirm : Modal

    data object ConfirmExport : Modal

    data object ExportInProgress : Modal


    @Immutable
    data class SelectSdModel(val models: List<String>, val selected: String) : Modal

    @Immutable
    data class Generating(val status: LocalDiffusion.Status? = null) : Modal {
        val pair: Pair<Int, Int>?
            get() = status?.let { (current, total) -> current to total }
    }

    @Immutable
    data class Communicating(
        val canCancel: Boolean = true,
        val hordeProcessStatus: HordeProcessStatus? = null,
    ) : Modal

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

    @Immutable
    data class EditTag(
        val prompt: String,
        val negativePrompt: String,
        val tag: String,
        val isNegative: Boolean = false
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
    data class DeleteLocalModelConfirm(val model: ServerSetupState.LocalModel) : Modal

    @Immutable
    data class Error(val error: UiText) : Modal

    data object Language : Modal
}
