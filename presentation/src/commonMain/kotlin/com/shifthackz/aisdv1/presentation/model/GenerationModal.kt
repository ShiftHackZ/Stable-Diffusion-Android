package com.shifthackz.aisdv1.presentation.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus

/**
 * Defines the `GenerationModal` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GenerationModal {

    /**
     * Provides the `None` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : GenerationModal

    /**
     * Defines the `Background` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Background : GenerationModal {
        /**
         * Provides the `Running` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object Running : Background
        /**
         * Provides the `Scheduled` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        data object Scheduled : Background
    }

    /**
     * Carries `Generating` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    @Immutable
    data class Generating(
        /**
         * Exposes the `canCancel` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val canCancel: Boolean = false,
        /**
         * Exposes the `status` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val status: LocalDiffusionStatus? = null,
    ) : GenerationModal {
        val pair: Pair<Int, Int>?
            get() = status?.let { (current, total) -> current to total }
    }

    /**
     * Carries `Communicating` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    @Immutable
    data class Communicating(
        /**
         * Exposes the `canCancel` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val canCancel: Boolean = true,
        /**
         * Exposes the `hordeProcessStatus` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val hordeProcessStatus: HordeProcessStatus? = null,
    ) : GenerationModal

    /**
     * Carries `Error` data through the SDAI presentation layer.
     *
     * @param error error value consumed by the API.
     * @author Dmitriy Moroz
     */
    @Immutable
    data class Error(val error: UiText) : GenerationModal

    /**
     * Defines the `Image` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Image : GenerationModal {

        /**
         * Carries `Single` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        @Immutable
        data class Single(
            /**
             * Exposes the `result` value used by the SDAI presentation layer.
             *
             * @author Dmitriy Moroz
             */
            val result: AiGenerationResult,
            /**
             * Exposes the `autoSaveEnabled` value used by the SDAI presentation layer.
             *
             * @author Dmitriy Moroz
             */
            val autoSaveEnabled: Boolean,
            /**
             * Exposes the `reportEnabled` value used by the SDAI presentation layer.
             *
             * @author Dmitriy Moroz
             */
            val reportEnabled: Boolean,
        ) : Image

        /**
         * Carries `Batch` data through the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        @Immutable
        data class Batch(
            /**
             * Exposes the `results` value used by the SDAI presentation layer.
             *
             * @author Dmitriy Moroz
             */
            val results: List<AiGenerationResult>,
            /**
             * Exposes the `autoSaveEnabled` value used by the SDAI presentation layer.
             *
             * @author Dmitriy Moroz
             */
            val autoSaveEnabled: Boolean,
        ) : Image

        /**
         * Provides the `companion object` singleton used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        companion object {
            /**
             * Creates the SDAI value produced by `create`.
             *
             * @param list list value consumed by the API.
             * @param autoSaveEnabled auto save enabled value consumed by the API.
             * @param reportEnabled report enabled value consumed by the API.
             * @author Dmitriy Moroz
             */
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
