package com.shifthackz.aisdv1.presentation.model

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState

sealed interface Modal {

    data object None : Modal

    data object LoadingRandomImage : Modal

    data object ClearAppCache : Modal

    data class DeleteImageConfirm(
        val isAll: Boolean,
        val isMultiple: Boolean,
    ) : Modal

    data class ConfirmExport(val exportAll: Boolean) : Modal

    data object ExportInProgress : Modal

    data object ConnectLocalHost : Modal

    sealed interface Background : Modal {
        data object Running : Background
        data object Scheduled : Background
    }

    @Immutable
    data class SelectSdModel(val models: List<String>, val selected: String) : Modal

    @Immutable
    data class Generating(
        val canCancel: Boolean = false,
        val status: LocalDiffusionStatus? = null,
    ) : Modal {
        val pair: Pair<Int, Int>?
            get() = status?.let { (current, total) -> current to total }
    }

    @Immutable
    data class Communicating(
        val canCancel: Boolean = true,
        val hordeProcessStatus: HordeProcessStatus? = null,
    ) : Modal

    data class PromptBottomSheet(
        val source: AiGenerationResult.Type,
    ) : Modal

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
        data class Single(val result: AiGenerationResult, val autoSaveEnabled: Boolean) : Image

        @Immutable
        data class Batch(val results: List<AiGenerationResult>, val autoSaveEnabled: Boolean) : Image

        @Immutable
        data class Crop(val bitmap: Bitmap) : Image

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

    @Immutable
    data class ManualPermission(val permission: UiText): Modal

    data object ClearInPaintConfirm : Modal

    data object Language : Modal

    data class LDScheduler(val scheduler: SchedulersToken) : Modal

    data class GalleryGrid(val grid: Grid) : Modal
}
