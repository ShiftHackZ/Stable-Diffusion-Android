package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

/**
 * Defines the `TextToImageIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface TextToImageIntent : MviIntent {
    /**
     * Provides the `OpenDrawer` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenDrawer : TextToImageIntent
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : TextToImageIntent
    /**
     * Provides the `ConfigureProvider` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ConfigureProvider : TextToImageIntent
    /**
     * Provides the `Generate` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Generate : TextToImageIntent
    /**
     * Provides the `DismissModal` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissModal : TextToImageIntent
    /**
     * Provides the `CancelGeneration` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object CancelGeneration : TextToImageIntent
    /**
     * Provides the `DismissError` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissError : TextToImageIntent
    /**
     * Provides the `DismissMessage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissMessage : TextToImageIntent
    /**
     * Provides the `DismissEditTag` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissEditTag : TextToImageIntent
    /**
     * Carries `SaveResult` data through the SDAI presentation layer.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    data class SaveResult(val base64: String) : TextToImageIntent
    /**
     * Carries `ShareResult` data through the SDAI presentation layer.
     *
     * @param base64 Base64 image payload used by the operation.
     * @author Dmitriy Moroz
     */
    data class ShareResult(val base64: String) : TextToImageIntent
    /**
     * Carries `SaveGenerationResults` data through the SDAI presentation layer.
     *
     * @param results results value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class SaveGenerationResults(val results: List<AiGenerationResult>) : TextToImageIntent
    /**
     * Carries `ViewGenerationResult` data through the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ViewGenerationResult(val result: AiGenerationResult) : TextToImageIntent
    /**
     * Carries `ReportGenerationResult` data through the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ReportGenerationResult(val result: AiGenerationResult) : TextToImageIntent
    /**
     * Carries `ShowEditTag` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class ShowEditTag(
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
    ) : TextToImageIntent
    /**
     * Carries `ApplyPrompts` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class ApplyPrompts(
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
    ) : TextToImageIntent
    /**
     * Carries `ApplyGenerationResult` data through the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data class ApplyGenerationResult(
        /**
         * Exposes the `ai` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        val ai: AiGenerationResult,
    ) : TextToImageIntent
    /**
     * Carries `UpdateAdvancedOptionsVisibility` data through the SDAI presentation layer.
     *
     * @param visible visible value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateAdvancedOptionsVisibility(val visible: Boolean) : TextToImageIntent
    /**
     * Carries `UpdatePrompt` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdatePrompt(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateNegativePrompt` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateNegativePrompt(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateWidth` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateWidth(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateHeight` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateHeight(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateSamplingSteps` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSamplingSteps(val value: Int) : TextToImageIntent
    /**
     * Carries `UpdateCfgScale` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateCfgScale(val value: Float) : TextToImageIntent
    /**
     * Carries `UpdateRestoreFaces` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateRestoreFaces(val value: Boolean) : TextToImageIntent
    /**
     * Carries `UpdateSeed` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSeed(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateSubSeed` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSubSeed(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateSubSeedStrength` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSubSeedStrength(val value: Float) : TextToImageIntent
    /**
     * Carries `UpdateSampler` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateSampler(val value: String) : TextToImageIntent
    /**
     * Carries `UpdateNsfw` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateNsfw(val value: Boolean) : TextToImageIntent
    /**
     * Carries `UpdateBatchCount` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateBatchCount(val value: Int) : TextToImageIntent
    /**
     * Carries `UpdateOpenAiModel` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiModel(val value: OpenAiModel) : TextToImageIntent
    /**
     * Carries `UpdateOpenAiSize` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiSize(val value: OpenAiSize) : TextToImageIntent
    /**
     * Carries `UpdateOpenAiQuality` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateOpenAiQuality(val value: OpenAiQuality) : TextToImageIntent
    /**
     * Carries `UpdateStabilityAiStyle` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateStabilityAiStyle(val value: StabilityAiStylePreset) : TextToImageIntent
    /**
     * Carries `UpdateStabilityAiClipGuidance` data through the SDAI presentation layer.
     *
     * @param value value value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class UpdateStabilityAiClipGuidance(val value: StabilityAiClipGuidance) : TextToImageIntent
}
