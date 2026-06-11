package com.shifthackz.aisdv1.presentation.widget.input

import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.FalAiAcceleration
import com.shifthackz.aisdv1.domain.entity.FalAiImageSize
import com.shifthackz.aisdv1.domain.entity.FalAiModel
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

/**
 * Defines the `GenerationInputFormEvent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GenerationInputFormEvent {
    /**
     * Carries `UpdateAdvancedOptionsVisibility` data through the SDAI presentation layer.
     *
     * @param visible visible value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateAdvancedOptionsVisibility(val visible: Boolean) : GenerationInputFormEvent
    /**
     * Carries `UpdatePrompt` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdatePrompt(val value: String) : GenerationInputFormEvent
    /**
     * Carries `UpdateNegativePrompt` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateNegativePrompt(val value: String) : GenerationInputFormEvent
    /**
     * Carries `EditTag` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class EditTag(
        /**
         * Exposes the `prompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val prompt: String,
        /**
         * Exposes the `negativePrompt` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val negativePrompt: String,
        /**
         * Exposes the `tag` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val tag: String,
        /**
         * Exposes the `isNegative` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val isNegative: Boolean,
    ) : GenerationInputFormEvent

    /**
     * Carries `UpdateWidth` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateWidth(val value: String) : GenerationInputFormEvent
    /**
     * Provides the `SwapDimensions` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object SwapDimensions : GenerationInputFormEvent
    /**
     * Carries `ApplyAspectRatio` data through the SDAI presentation layer.
     *
     * @param ratio ratio value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ApplyAspectRatio(val ratio: GenerationAspectRatio) : GenerationInputFormEvent
    /**
     * Carries `UpdateHeight` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHeight(val value: String) : GenerationInputFormEvent
    /**
     * Carries `UpdateSamplingSteps` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSamplingSteps(val value: Int) : GenerationInputFormEvent
    /**
     * Carries `UpdateCfgScale` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateCfgScale(val value: Float) : GenerationInputFormEvent
    /**
     * Carries `UpdateRestoreFaces` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateRestoreFaces(val value: Boolean) : GenerationInputFormEvent
    /**
     * Carries `UpdateSeed` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSeed(val value: String) : GenerationInputFormEvent
    /**
     * Carries `UpdateSubSeed` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSubSeed(val value: String) : GenerationInputFormEvent
    /**
     * Carries `UpdateSubSeedStrength` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSubSeedStrength(val value: Float) : GenerationInputFormEvent
    /**
     * Carries `UpdateSampler` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSampler(val value: String) : GenerationInputFormEvent
    /**
     * Carries `UpdateScheduler` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateScheduler(val value: Scheduler) : GenerationInputFormEvent
    /**
     * Carries `UpdateForgeModules` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateForgeModules(val value: List<ForgeModule>) : GenerationInputFormEvent
    /**
     * Carries `UpdateNsfw` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateNsfw(val value: Boolean) : GenerationInputFormEvent
    /**
     * Carries `UpdateBatch` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateBatch(val value: Int) : GenerationInputFormEvent
    /**
     * Carries `UpdateOpenAiModel` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiModel(val value: OpenAiModel) : GenerationInputFormEvent
    /**
     * Carries `UpdateOpenAiSize` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiSize(val value: OpenAiSize) : GenerationInputFormEvent
    /**
     * Carries `UpdateOpenAiQuality` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiQuality(val value: OpenAiQuality) : GenerationInputFormEvent
    /**
     * Carries `UpdateFalAiModel` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateFalAiModel(val value: FalAiModel) : GenerationInputFormEvent
    /**
     * Carries `UpdateFalAiImageSize` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateFalAiImageSize(val value: FalAiImageSize) : GenerationInputFormEvent
    /**
     * Carries `UpdateFalAiAcceleration` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateFalAiAcceleration(val value: FalAiAcceleration) : GenerationInputFormEvent
    /**
     * Carries `UpdateFalAiSyncMode` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateFalAiSyncMode(val value: Boolean) : GenerationInputFormEvent
    /**
     * Carries `UpdateStabilityAiStyle` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateStabilityAiStyle(val value: StabilityAiStylePreset) : GenerationInputFormEvent
    /**
     * Carries `UpdateStabilityAiClipGuidance` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateStabilityAiClipGuidance(val value: StabilityAiClipGuidance) : GenerationInputFormEvent
    /**
     * Carries `UpdateHiresConfig` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHiresConfig(val value: HiresConfig) : GenerationInputFormEvent
    /**
     * Carries `UpdateADetailerConfig` data through the SDAI presentation layer.
     *
     * @param value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateADetailerConfig(val value: ADetailerConfig) : GenerationInputFormEvent
    /**
     * Provides the `RefreshADetailerAvailability` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object RefreshADetailerAvailability : GenerationInputFormEvent
    /**
     * Provides the `OpenADetailerInstallInstructions` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenADetailerInstallInstructions : GenerationInputFormEvent
}

/**
 * Carries `GenerationAspectRatio` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
enum class GenerationAspectRatio(
    /**
     * Exposes the `displayName` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val displayName: String,
    /**
     * Exposes the `width` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val width: Int,
    /**
     * Exposes the `height` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val height: Int,
) {
    SQUARE("1:1", 1, 1),
    PORTRAIT("2:3", 2, 3),
    PHOTO_PORTRAIT("3:4", 3, 4),
    WIDE("16:9", 16, 9),
    LANDSCAPE("3:2", 3, 2),
    PHOTO_LANDSCAPE("4:3", 4, 3),
}
