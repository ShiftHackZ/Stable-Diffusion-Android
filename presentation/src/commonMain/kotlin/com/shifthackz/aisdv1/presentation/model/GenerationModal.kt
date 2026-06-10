package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus

sealed interface GenerationModal {

    data object None : GenerationModal

    sealed interface Background : GenerationModal {
        data object Running : Background
        data object Scheduled : Background
    }

    @Immutable
    data class Generating(
        val canCancel: Boolean = false,
        val status: LocalDiffusionStatus? = null,
    ) : GenerationModal {
        val pair: Pair<Int, Int>?
            get() = status?.let { (current, total) -> current to total }
    }

    @Immutable
    data class Communicating(
        val canCancel: Boolean = true,
        val hordeProcessStatus: HordeProcessStatus? = null,
    ) : GenerationModal

    @Immutable
    data class Error(val error: UiText) : GenerationModal

    sealed interface Image : GenerationModal {

        @Immutable
        data class Single(
            val result: AiGenerationResult,
            val autoSaveEnabled: Boolean,
            val reportEnabled: Boolean,
        ) : Image

        @Immutable
        data class Batch(
            val results: List<AiGenerationResult>,
            val autoSaveEnabled: Boolean,
        ) : Image

        companion object {
            fun create(
                list: List<AiGenerationResult>,
                autoSaveEnabled: Boolean,
                reportEnabled: Boolean = false,
            ): Image = if (list.size > 1) {
                Batch(list, autoSaveEnabled)
            } else {
                Single(list.first(), autoSaveEnabled, reportEnabled)
            }
        }
    }
}
