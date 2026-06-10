package com.shifthackz.aisdv1.presentation.widget.input

import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
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
}
